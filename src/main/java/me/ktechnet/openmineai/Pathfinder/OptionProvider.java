package me.ktechnet.openmineai.Pathfinder;

import me.ktechnet.openmineai.Helpers.AdjacentBlocksHelper;
import me.ktechnet.openmineai.Helpers.BrokenBlocksHelper;
import me.ktechnet.openmineai.Helpers.NodeTypeRules;
import me.ktechnet.openmineai.Helpers.PlacedBlocksHelper;
import me.ktechnet.openmineai.Models.Classes.Option;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.AvoidBlocks;
import me.ktechnet.openmineai.Models.ConfigData.CostResolve;
import me.ktechnet.openmineai.Models.ConfigData.PassableBlocks;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OptionProvider implements IOptionProvider {
    private final INode parent;

    private final NodeTypeRules r;

    private final IRuleEvaluator rev;

    private Pos parentPos;

    private Pos grandparent;

    private Pos greatgrandparent;

    private Pos entry;

    private final Pos dest;

    public OptionProvider(INode parent) {
        this.parent = parent;
        //Get data from 4 previous nodes as these are the most likely to effect us
        this.parentPos = this.parent.pos();
        this.grandparent = this.parent.pos();
        this.greatgrandparent = this.parent.pos();
        this.entry = this.parent.pos();
        ArrayList<Pos> Broken = new BrokenBlocksHelper().Broken(this.parent.myType(), this.parent.pos());
        ArrayList<Pos> Placed = new PlacedBlocksHelper().Placed(this.parent.myType(), this.parent.pos());
        if (this.parent.parent() != null) {
            entry = this.parent.parent().pos();
            Broken.addAll(new BrokenBlocksHelper().Broken(this.parent.parent().myType(), entry));
            Placed.addAll(new PlacedBlocksHelper().Placed(this.parent.parent().myType(), entry));
            if (this.parent.parent().parent() != null) {
                grandparent = this.parent.parent().parent().pos();
                Broken.addAll(new BrokenBlocksHelper().Broken(this.parent.parent().parent().myType(), grandparent));
                Placed.addAll(new PlacedBlocksHelper().Placed(this.parent.parent().parent().myType(), grandparent));
                if (this.parent.parent().parent().parent() != null) {
                    greatgrandparent = this.parent.parent().parent().parent().pos();
                    Broken.addAll(new BrokenBlocksHelper().Broken(this.parent.parent().parent().parent().myType(), greatgrandparent));
                    Placed.addAll(new PlacedBlocksHelper().Placed(this.parent.parent().parent().parent().myType(), greatgrandparent));
                }
            }
        }
        this.r = new NodeTypeRules();
        this.rev = new RuleEvaluator(Broken, Placed, parentPos, this.parent.master().settings());
        this.dest = this.parent.master().destination();
    }

    @Override
    public INode parent() {
        return parent;
    }

    @Override
    public IOption EvaluatePosition(Pos pos, boolean diagonal, Pos artificalParent) {
        if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent) || pos.IsEqual(greatgrandparent)) {
            return null;
        }

        if (artificalParent != null) parentPos = artificalParent;

        ArrayList<IOption> candidates = new ArrayList<>();

        if (pos.IsEqual(this.parent.master().destination()) && !diagonal) {
            return new Option(0, NodeType.DESTINATION, pos, null);
        }

        if (pos.y - parentPos.y == 1) {
            //Ascend
            if (rev.Evaluate(pos, r.GetBreakAndTower())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.ASCEND_BREAK_AND_TOWER, pos, dest), NodeType.ASCEND_BREAK_AND_TOWER, pos, null));

            } else if (rev.Evaluate(pos, r.GetLadder())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.ASCEND, pos, dest), NodeType.ASCEND, pos, null));

            }else if (rev.Evaluate(pos, r.GetTower())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.ASCEND_TOWER, pos, dest), NodeType.ASCEND_TOWER, pos, null));

            }
        } else if (pos.y - parentPos.y == -1) {
            //Descend
            if (rev.Evaluate(pos, r.GetRareDrop())) { //Should never be encountered, but still going to put a drop node in here just in case
                candidates.add(new Option(CostResolve.Resolve(NodeType.DROP, pos, dest), NodeType.DROP, pos, null));

            } else if (rev.Evaluate(pos, r.GetLadder())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.DESCEND, pos, dest), NodeType.DESCEND, pos, null));

            } else if (rev.Evaluate(pos, r.GetDescentMine())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.DESCEND_MINE, pos, dest), NodeType.DESCEND_MINE, pos, null));

            }
        } else {
            //Side nodes
            if (rev.Evaluate(pos, r.GetMove(diagonal))) {
                //Walk floor
                candidates.add(new Option(CostResolve.Resolve(NodeType.MOVE, pos, dest), NodeType.MOVE, pos, null));

            } else if (rev.Evaluate(pos, r.GetStepUp(diagonal))) {
                //Step up
                pos.y++;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent) || pos.IsEqual(greatgrandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                candidates.add(new Option(CostResolve.Resolve(NodeType.STEP_UP, pos, dest), NodeType.STEP_UP, pos, null));

            } else if (rev.Evaluate(pos, r.GetStepDown(diagonal))) {
                //Step down
                pos.y--;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent)  || pos.IsEqual(greatgrandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                candidates.add(new Option(CostResolve.Resolve(NodeType.STEP_DOWN, pos, dest), NodeType.STEP_DOWN, pos, null));

            } else if (rev.Evaluate(pos, r.GetBreakAndMove()) && !diagonal) {
                //Break and move into
                candidates.add(new Option(CostResolve.Resolve(NodeType.BREAK_AND_MOVE, pos, dest), NodeType.BREAK_AND_MOVE, pos, null));

            }else if (rev.Evaluate(pos, r.GetStepUpAndBreak()) && !diagonal) {
                //Step up and break
                pos.y++;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent) || pos.IsEqual(greatgrandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                candidates.add(new Option(CostResolve.Resolve(NodeType.STEP_UP_AND_BREAK, pos, dest), NodeType.STEP_UP_AND_BREAK, pos, null));

            } else if (rev.Evaluate(pos, r.GetStepDownAndBreak()) && !diagonal) {
                //Step down and break
                pos.y--;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent) || pos.IsEqual(greatgrandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                candidates.add(new Option(CostResolve.Resolve(NodeType.STEP_DOWN_AND_BREAK, pos, dest), NodeType.STEP_DOWN_AND_BREAK, pos, null));

            } else if (rev.Evaluate(pos, r.GetSwim(diagonal))) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.SWIM, pos, dest), NodeType.SWIM, pos, null));

            } else if (rev.Evaluate(pos, r.GetLiquidBridge()) && !diagonal) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.BRIDGE, pos, dest), NodeType.BRIDGE, pos, null));

            } else if (rev.Evaluate(pos, r.GetDecentOrParkourOrBridge(diagonal))) {
                if (!diagonal && this.parent.master().settings().allowPlace) candidates.add(new Option(CostResolve.Resolve(NodeType.BRIDGE, pos, dest), NodeType.BRIDGE, pos, null));
                BlockPos bottom = GetBlockBeneath(pos);
                if (bottom != null) {
                    Pos bPos = new Pos(bottom.getX(), bottom.getY() + 1, bottom.getZ());
                    int dist = pos.y - bottom.getY();
                    Block b = Minecraft.getMinecraft().world.getBlockState(bottom).getBlock();
                    if ((dist <= 10 || (b == Blocks.WATER || b == Blocks.FLOWING_WATER) || parent.master().settings().hasWaterBucket) && !AvoidBlocks.blocks.contains(b))
                        if (!bPos.IsEqual(entry) && !bPos.IsEqual(this.parent.pos()) && !bPos.IsEqual(grandparent) && !bPos.IsEqual(greatgrandparent))
                            candidates.add(new Option(CostResolve.Resolve(NodeType.DROP, new Pos(bottom.getX(), bottom.getY() + 1, bottom.getZ()), dest), NodeType.DROP, pos, artificalParent));
                }
                ArrayList<IParkourOption> parkourOptions = new ParkourProvider().GetParkourLocations(pos, artificalParent, dest); //Note: Parkour nodes will always be in the air, and the executor starts executing them while on the previous solid block
                if (parkourOptions.size() > 0) {
                    parkourOptions.sort(Comparator.comparingDouble(IParkourOption::Cost));
                    if (parkourOptions.get(0).Cost() > parkourOptions.get(parkourOptions.size() - 1).Cost()) Collections.reverse(parkourOptions);
                    IParkourOption prkO = parkourOptions.get(0);
                    candidates.add(new Option(CostResolve.Resolve(NodeType.PARKOUR, prkO.pos(), dest), NodeType.PARKOUR, pos, artificalParent));
                }
            }
        }
        if (candidates.size() > 0) {
            candidates.sort(Comparator.comparingDouble(IOption::cost));
            if (candidates.get(0).cost() > candidates.get(candidates.size() - 1).cost()) Collections.reverse(candidates);
            return candidates.get(0);
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<IOption> EvaluateOptions() {
        Pos pos = parent.pos();
        if (AdjacentBlocksHelper.AdjacentOutOfChunk(pos)) return null;
        ArrayList<IOption> list = new ArrayList<>();
        if (parent.myType() == NodeType.DROP) {
            BlockPos bPos = GetBlockBeneath(pos);
            if (bPos == null) return null;
            pos = new Pos(bPos.getX(), bPos.getY() + 1, bPos.getZ());
            if (pos.IsEqual(parent.master().destination())) {
                Pos finalPos = pos;
                return new ArrayList<IOption>() {
                    {
                        add(new Option(0, NodeType.DESTINATION, finalPos, null));
                    }
                };
            }
        } else if (parent.myType() == NodeType.PARKOUR) {
            Pos artParent = parent.artificialParent() != null ? parent.artificialParent() : parent.parent().pos();
            ArrayList<IParkourOption> parkourOptions = new ParkourProvider().GetParkourLocations(parent.pos(), artParent, dest);
            if (parkourOptions.size() > 0) {
                parkourOptions.sort(Comparator.comparingDouble(IParkourOption::Cost));
                if (parkourOptions.get(0).Cost() > parkourOptions.get(parkourOptions.size() - 1).Cost()) Collections.reverse(parkourOptions);
                IParkourOption prkO = parkourOptions.get(0);
                pos = new Pos(prkO.pos().x, prkO.pos().y, prkO.pos().z);
                if (pos.IsEqual(parent.master().destination())) {
                    Pos finalPos = pos;
                    return new ArrayList<IOption>() {
                        {
                            add(new Option(0, NodeType.DESTINATION, finalPos, null));
                        }
                    };
                }
            }
        }
        list.add(EvaluatePosition(new Pos(pos.x + 1, pos.y, pos.z), false, pos));
        list.add(EvaluatePosition(new Pos(pos.x + -1, pos.y, pos.z), false, pos));
        list.add(EvaluatePosition(new Pos(pos.x, pos.y + 1, pos.z), false, pos));
        list.add(EvaluatePosition(new Pos(pos.x, pos.y - 1, pos.z), false, pos));
        list.add(EvaluatePosition(new Pos(pos.x, pos.y, pos.z + 1), false, pos));
        list.add(EvaluatePosition(new Pos(pos.x, pos.y, pos.z - 1), false, pos));
        list.add(EvaluatePosition(new Pos(pos.x + 1, pos.y, pos.z + 1), true, pos));
        list.add(EvaluatePosition(new Pos(pos.x + 1, pos.y, pos.z - 1), true, pos));
        list.add(EvaluatePosition(new Pos(pos.x - 1, pos.y, pos.z + 1), true, pos));
        list.add(EvaluatePosition(new Pos(pos.x - 1, pos.y, pos.z - 1), true, pos));
        list.removeAll(Collections.singleton(null));
        return list;
    }

    private BlockPos GetBlockBeneath(Pos start)
    {
        for (int i = start.y; i >= 0; i--) {
            BlockPos bPos = new BlockPos(start.x, i, start.z);
            Block b = Minecraft.getMinecraft().world.getBlockState(bPos).getBlock();
            if (!PassableBlocks.blocks.contains(b)) return bPos;
        }
        return null;
    }
}
