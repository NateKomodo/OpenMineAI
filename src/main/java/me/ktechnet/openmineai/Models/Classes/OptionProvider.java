package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Helpers.BrokenBlocksHelper;
import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.NodeTypeRules;
import me.ktechnet.openmineai.Helpers.PlacedBlocksHelper;
import me.ktechnet.openmineai.Main;
import me.ktechnet.openmineai.Models.ConfigData.AvoidBlocks;
import me.ktechnet.openmineai.Models.ConfigData.CostResolve;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IOption;
import me.ktechnet.openmineai.Models.Interfaces.IOptionProvider;
import me.ktechnet.openmineai.Models.Interfaces.IRuleEvaluator;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OptionProvider implements IOptionProvider {
    private INode parent;

    private NodeTypeRules r;

    private IRuleEvaluator rev;

    private Pos parentPos;

    private Pos grandparent;

    private Pos greatgrandparent;

    private Pos entry;

    private Pos dest;

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
        this.rev = new RuleEvaluator(Broken, Placed, parentPos);
        this.dest = this.parent.master().destination();
    }

    @Override
    public INode parent() {
        return parent;
    }

    @Override
    public IOption EvaluatePosition(Pos pos, boolean diagonal) { //TODO parkour handler, check chunk is loaded, fire, doors/interactable
        if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent) || pos.IsEqual(greatgrandparent)) {
            return null;
        }

        ArrayList<IOption> candidates = new ArrayList<>();

        if (pos.IsEqual(this.parent.master().destination()) && !diagonal) {
            return new Option(0, NodeType.DESTINATION, pos);
        }

        if (pos.y - parentPos.y == 1) {
            //Ascend
            if (rev.Evaluate(pos, r.GetBreakAndTower())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.ASCEND_BREAK_AND_TOWER, pos, dest), NodeType.ASCEND_BREAK_AND_TOWER, pos));

            } else if (rev.Evaluate(pos, r.GetLadder())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.ASCEND, pos, dest), NodeType.ASCEND, pos));

            }else if (rev.Evaluate(pos, r.GetTower())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.ASCEND_TOWER, pos, dest), NodeType.ASCEND_TOWER, pos));

            }
        } else if (pos.y - parentPos.y == -1) {
            //Descend
            if (rev.Evaluate(pos, r.GetRareDrop())) { //Should never be encountered, but still going to put a drop node in here just in case
                candidates.add(new Option(CostResolve.Resolve(NodeType.DROP, pos, dest), NodeType.DROP, pos));

            } else if (rev.Evaluate(pos, r.GetLadder())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.DESCEND, pos, dest), NodeType.DESCEND, pos));

            } else if (rev.Evaluate(pos, r.GetDescentMine())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.DESCEND_MINE, pos, dest), NodeType.DESCEND_MINE, pos));

            }
        } else {
            //Side nodes
            if (rev.Evaluate(pos, r.GetMove(diagonal))) {
                //Walk floor
                candidates.add(new Option(CostResolve.Resolve(NodeType.MOVE, pos, dest), NodeType.MOVE, pos));

            } else if (rev.Evaluate(pos, r.GetStepUp(diagonal))) {
                //Step up
                pos.y++;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent) || pos.IsEqual(greatgrandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                candidates.add(new Option(CostResolve.Resolve(NodeType.STEP_UP, pos, dest), NodeType.STEP_UP, pos));

            } else if (rev.Evaluate(pos, r.GetStepDown(diagonal))) {
                //Step down
                pos.y--;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent)  || pos.IsEqual(greatgrandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                candidates.add(new Option(CostResolve.Resolve(NodeType.STEP_DOWN, pos, dest), NodeType.STEP_DOWN, pos));

            } else if (rev.Evaluate(pos, r.GetBreakAndMove()) && !diagonal) {
                //Break and move into
                candidates.add(new Option(CostResolve.Resolve(NodeType.BREAK_AND_MOVE, pos, dest), NodeType.BREAK_AND_MOVE, pos));

            }else if (rev.Evaluate(pos, r.GetStepUpAndBreak()) && !diagonal) {
                //Step up and break
                pos.y++;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent) || pos.IsEqual(greatgrandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                candidates.add(new Option(CostResolve.Resolve(NodeType.STEP_UP_AND_BREAK, pos, dest), NodeType.STEP_UP_AND_BREAK, pos));

            } else if (rev.Evaluate(pos, r.GetStepDownAndBreak()) && !diagonal) {
                //Step down and break
                pos.y--;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent) || pos.IsEqual(greatgrandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                candidates.add(new Option(CostResolve.Resolve(NodeType.STEP_DOWN_AND_BREAK, pos, dest), NodeType.STEP_DOWN_AND_BREAK, pos));

            } else if (rev.Evaluate(pos, r.GetSwim(diagonal))) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.SWIM, pos, dest), NodeType.SWIM, pos));

            } else if (rev.Evaluate(pos, r.GetLiquidBridge()) && !diagonal) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.BRIDGE, pos, dest), NodeType.BRIDGE, pos));

            } else if (rev.Evaluate(pos, r.GetDecentOrParkourOrBridge(diagonal))) {
                if (!diagonal) candidates.add(new Option(CostResolve.Resolve(NodeType.BRIDGE, pos, dest), NodeType.BRIDGE, pos));
                BlockPos bottom = GetBlockBeneath(pos);
                if (bottom != null) {
                    Pos bPos = new Pos(bottom.getX(), bottom.getY() + 1, bottom.getZ());
                    int dist = pos.y - bottom.getY();
                    Block b = Minecraft.getMinecraft().world.getBlockState(bottom).getBlock();
                    if ((dist <= 10 || (b == Blocks.WATER || b == Blocks.FLOWING_WATER) || parent.master().settings().hasWaterBucket) && !AvoidBlocks.blocks.contains(b))
                        if (!bPos.IsEqual(entry) && !bPos.IsEqual(this.parent.pos()) && !bPos.IsEqual(grandparent) && !bPos.IsEqual(greatgrandparent))
                            candidates.add(new Option(CostResolve.Resolve(NodeType.DROP, new Pos(bottom.getX(), bottom.getY() + 1, bottom.getZ()), dest), NodeType.DROP, pos));
                }
                //TODO parkour
                //Update: as we now eval all, we can split these up
                //Note: Parkour nodes will always be in the air, and the executor starts executing them while on the previous solid block
            }
        }
        if (candidates.size() > 0) {
            Collections.sort(candidates, new Comparator<IOption>() {
                @Override
                public int compare(IOption o1, IOption o2) {
                    return Double.compare(o1.cost(), o2.cost());
                }
            });
            if (candidates.get(0).cost() > candidates.get(candidates.size() - 1).cost()) Collections.reverse(candidates);
            return candidates.get(0);
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<IOption> EvaluateOptions() {
        Pos pos = parent.pos();
        ArrayList<IOption> list = new ArrayList<>();
        if (!(parent.myType() == NodeType.PARKOUR)) {
            if (parent.myType() == NodeType.DROP) {
                BlockPos bPos = GetBlockBeneath(pos);
                pos = new Pos(bPos.getX(), bPos.getY() + 1, bPos.getZ());
                if (pos.IsEqual(parent.master().destination())) {
                    Pos finalPos = pos;
                    return new ArrayList<IOption>() {
                        {
                            add(new Option(0, NodeType.DESTINATION, finalPos));
                        }
                    };
                }
            }
            list.add(EvaluatePosition(new Pos(pos.x + 1, pos.y, pos.z), false));
            list.add(EvaluatePosition(new Pos(pos.x + -1, pos.y, pos.z), false));
            list.add(EvaluatePosition(new Pos(pos.x, pos.y + 1, pos.z), false));
            list.add(EvaluatePosition(new Pos(pos.x, pos.y - 1, pos.z), false));
            list.add(EvaluatePosition(new Pos(pos.x, pos.y, pos.z + 1), false));
            list.add(EvaluatePosition(new Pos(pos.x, pos.y, pos.z - 1), false));
            list.add(EvaluatePosition(new Pos(pos.x + 1, pos.y, pos.z + 1), true));
            list.add(EvaluatePosition(new Pos(pos.x + 1, pos.y, pos.z - 1), true));
            list.add(EvaluatePosition(new Pos(pos.x - 1, pos.y, pos.z + 1), true));
            list.add(EvaluatePosition(new Pos(pos.x - 1, pos.y, pos.z - 1), true));
        } else {
            //TODO parkour
        }
        list.removeAll(Collections.singleton(null));
        return list;
    }

    private BlockPos GetBlockBeneath(Pos start)
    {
        for (int i = start.y; i >= 0; i--) {
            BlockPos bPos = new BlockPos(start.x, i, start.z);
            Block b = Minecraft.getMinecraft().world.getBlockState(bPos).getBlock();
            if (b != Blocks.AIR) return bPos;
        }
        return null;
    }
}
