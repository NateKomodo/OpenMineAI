package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Helpers.AdjacentBlocksHelper;
import me.ktechnet.openmineai.Helpers.AdjacentLavaHelper;
import me.ktechnet.openmineai.Helpers.BrokenBlocksHelper;
import me.ktechnet.openmineai.Helpers.NodeTypeRules;
import me.ktechnet.openmineai.Models.ConfigData.AvoidBlocks;
import me.ktechnet.openmineai.Models.ConfigData.CostResolve;
import me.ktechnet.openmineai.Models.ConfigData.PassableBlocks;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IOption;
import me.ktechnet.openmineai.Models.Interfaces.IOptionProvider;
import me.ktechnet.openmineai.Models.Interfaces.IRuleEvaluator;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;

import java.util.ArrayList;
import java.util.Collections;

public class OptionProvider implements IOptionProvider {
    private INode parent;

    public OptionProvider(INode parent) {
        this.parent = parent;
    }

    @Override
    public INode parent() {
        return parent;
    }

    @Override
    public IOption EvaluatePosition(Pos pos) { //TODO parkour handler, check chunk is loaded, diagonals
        //Get data from 3 previous nodes as these are the most likely to effect us
        Pos parent = this.parent.pos();
        Pos grandparent = this.parent.pos();
        Pos entry = new Pos(parent.x, parent.y, parent.z);
        ArrayList<Pos> Broken = new BrokenBlocksHelper().Broken(this.parent.myType(), this.parent.pos());
        if (this.parent.parent() != null) {
            entry = this.parent.parent().pos();
            Broken.addAll(new BrokenBlocksHelper().Broken(this.parent.parent().myType(), this.parent.parent().pos()));
            if (this.parent.parent().parent() != null) {
                grandparent = this.parent.parent().parent().pos();
                Broken.addAll(new BrokenBlocksHelper().Broken(this.parent.parent().myType(), this.parent.parent().pos()));
            }
        }

        Pos dest = this.parent.master().destination();

        if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent)) {
            return null;
        }

        NodeTypeRules r = new NodeTypeRules();
        IRuleEvaluator rev = new RuleEvaluator(Broken);

        if (pos.y - parent.y == 1) {
            //Ascend
            if (rev.Evaluate(pos, r.GetBreakAndTower())) {
                return new Option(CostResolve.Resolve(NodeType.ASCEND_BREAK_AND_TOWER, pos, dest), NodeType.ASCEND_BREAK_AND_TOWER, pos);

            } else if (rev.Evaluate(pos, r.GetTower())) {
                return new Option(CostResolve.Resolve(NodeType.ASCEND_TOWER, pos, dest), NodeType.ASCEND_TOWER, pos);

            } else if (rev.Evaluate(pos, r.GetLadder())) {
                return new Option(CostResolve.Resolve(NodeType.ASCEND, pos, dest), NodeType.ASCEND, pos);
            }
        } else if (pos.y - parent.y == -1) {
            //Descend
            if (rev.Evaluate(pos, r.GetRareDrop())) { //Should never be encountered, but still going to put a drop node in here just in case
                return new Option(CostResolve.Resolve(NodeType.DROP, pos, dest), NodeType.DROP, pos);

            } else if (rev.Evaluate(pos, r.GetDescentMine())) {
                return new Option(CostResolve.Resolve(NodeType.DESCEND_MINE, pos, dest), NodeType.DESCEND_MINE, pos);

            } else if (rev.Evaluate(pos, r.GetLadder())) {
                return new Option(CostResolve.Resolve(NodeType.DESCEND, pos, dest), NodeType.DESCEND, pos);
            }
        } else { //TODO swim
            //Side nodes
            if (rev.Evaluate(pos, r.GetMove())) {
                //Walk floor
                return new Option(CostResolve.Resolve(NodeType.MOVE, pos, dest), NodeType.MOVE, pos);

            } else if (rev.Evaluate(pos, r.GetStepUp())) {
                //Step up
                pos.y++;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                return new Option(CostResolve.Resolve(NodeType.STEP_UP, pos, dest), NodeType.STEP_UP, pos);

            //} else if (rev.Evaluate(pos, r.GetStepUpAndBreak())) {
            //    //Step up and break
            //    pos.y++;
            //    return new Option(CostResolve.Resolve(NodeType.STEP_UP_AND_BREAK, pos, dest), NodeType.STEP_UP_AND_BREAK, pos);
            //
            } else if (rev.Evaluate(pos, r.GetStepDown())) {
                //Step down
                pos.y--;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                return new Option(CostResolve.Resolve(NodeType.STEP_DOWN, pos, dest), NodeType.STEP_DOWN, pos);

            //} else if (rev.Evaluate(pos, r.GetStepDownAndBreak())) {
            //    //Step down and break
            //    pos.y--;
            //    return new Option(CostResolve.Resolve(NodeType.STEP_DOWN_AND_BREAK, pos, dest), NodeType.STEP_DOWN_AND_BREAK, pos);
            //
            } else if (rev.Evaluate(pos, r.GetBreakAndMove())) {
                //Break and move into
                return new Option(CostResolve.Resolve(NodeType.BREAK_AND_MOVE, pos, dest), NodeType.BREAK_AND_MOVE, pos);

            } else if (rev.Evaluate(pos, r.GetDecentOrParkourOrBridge())) {
                //TODO choose drop node or bridge, or parkour
                //Note: as previous node will be on a block, this node will always be in the air, and therefore the executor must know what to do before starting to move
            }
        }
        return null;
    }

    @Override
    public ArrayList<IOption> EvaluateOptions() {
        //TODO modify position due to previous node being descent
        Pos pos = parent.pos();
        ArrayList<IOption> list = new ArrayList<>();
        if (!(parent.myType() == NodeType.PARKOUR || parent.myType() == NodeType.BRIDGE_AND_PARKOUR || parent.myType() == NodeType.DROP)) {
            list.add(EvaluatePosition(new Pos(pos.x + 1, pos.y, pos.z)));
            list.add(EvaluatePosition(new Pos(pos.x + -1, pos.y, pos.z)));
            list.add(EvaluatePosition(new Pos(pos.x, pos.y + 1, pos.z)));
            list.add(EvaluatePosition(new Pos(pos.x, pos.y - 1, pos.z)));
            list.add(EvaluatePosition(new Pos(pos.x, pos.y, pos.z + 1)));
            list.add(EvaluatePosition(new Pos(pos.x, pos.y, pos.z - 1)));
        } else {
            //TODO get blocks to parkour to or the descent location
        }
        list.removeAll(Collections.singleton(null));
        return list;
    }
}
