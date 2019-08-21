package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Helpers.BrokenBlocksHelper;
import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.NodeTypeRules;
import me.ktechnet.openmineai.Models.ConfigData.CostResolve;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IOption;
import me.ktechnet.openmineai.Models.Interfaces.IOptionProvider;
import me.ktechnet.openmineai.Models.Interfaces.IRuleEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    public IOption EvaluatePosition(Pos pos, boolean diagonal) { //TODO parkour handler, check chunk is loaded, gravity blocks, water, fire
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
        IRuleEvaluator rev = new RuleEvaluator(Broken, parent);

        ArrayList<IOption> candidates = new ArrayList<>();

        if (pos.IsEqual(this.parent.master().destination())) {
            return new Option(0, NodeType.DESTINATION, pos);
        }

        if (pos.y - parent.y == 1) {
            //Ascend
            if (rev.Evaluate(pos, r.GetBreakAndTower())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.ASCEND_BREAK_AND_TOWER, pos, dest), NodeType.ASCEND_BREAK_AND_TOWER, pos));

            } else if (rev.Evaluate(pos, r.GetTower())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.ASCEND_TOWER, pos, dest), NodeType.ASCEND_TOWER, pos));

            } else if (rev.Evaluate(pos, r.GetLadder())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.ASCEND, pos, dest), NodeType.ASCEND, pos));
            }
        } else if (pos.y - parent.y == -1) {
            //Descend
            if (rev.Evaluate(pos, r.GetRareDrop())) { //Should never be encountered, but still going to put a drop node in here just in case
                ChatMessageHandler.SendMessage("Somehow, DROP was encountered");
                candidates.add(new Option(CostResolve.Resolve(NodeType.DROP, pos, dest), NodeType.DROP, pos));

            } else if (rev.Evaluate(pos, r.GetDescentMine())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.DESCEND_MINE, pos, dest), NodeType.DESCEND_MINE, pos));

            } else if (rev.Evaluate(pos, r.GetLadder())) {
                candidates.add(new Option(CostResolve.Resolve(NodeType.DESCEND, pos, dest), NodeType.DESCEND, pos));
            }
        } else { //TODO swim
            //Side nodes
            if (rev.Evaluate(pos, r.GetMove(diagonal))) {
                //Walk floor
                candidates.add(new Option(CostResolve.Resolve(NodeType.MOVE, pos, dest), NodeType.MOVE, pos));

            } else if (rev.Evaluate(pos, r.GetStepUp(diagonal))) {
                //Step up
                pos.y++;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                candidates.add(new Option(CostResolve.Resolve(NodeType.STEP_UP, pos, dest), NodeType.STEP_UP, pos));

            } else if (rev.Evaluate(pos, r.GetBreakAndMove()) && !diagonal) {
                //Break and move into
                candidates.add(new Option(CostResolve.Resolve(NodeType.BREAK_AND_MOVE, pos, dest), NodeType.BREAK_AND_MOVE, pos));

            } else if (rev.Evaluate(pos, r.GetStepDown(diagonal))) {
                //Step down
                pos.y--;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                candidates.add(new Option(CostResolve.Resolve(NodeType.STEP_DOWN, pos, dest), NodeType.STEP_DOWN, pos));

            } else if (rev.Evaluate(pos, r.GetStepUpAndBreak()) && !diagonal) {
                //Step up and break
                pos.y++;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                candidates.add(new Option(CostResolve.Resolve(NodeType.STEP_UP_AND_BREAK, pos, dest), NodeType.STEP_UP_AND_BREAK, pos));

            } else if (rev.Evaluate(pos, r.GetStepDownAndBreak()) && !diagonal) {
                //Step down and break
                pos.y--;
                if (pos.IsEqual(entry) || pos.IsEqual(this.parent.pos()) || pos.IsEqual(grandparent)) { //Check that we didnt just step up into an old pos. Also checks grandparent to prevent loop
                    return null;
                }
                candidates.add(new Option(CostResolve.Resolve(NodeType.STEP_DOWN_AND_BREAK, pos, dest), NodeType.STEP_DOWN_AND_BREAK, pos));

            } else if (rev.Evaluate(pos, r.GetDecentOrParkourOrBridge())) {
                //TODO choose drop node or bridge, or parkour
                //Update: as we now eval all, we can split these up
                //Note: as previous node will be on a block, this node will always be in the air, and therefore the executor must know what to do before starting to move
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
        //TODO modify position due to previous node being drop
        Pos pos = parent.pos();
        ArrayList<IOption> list = new ArrayList<>();
        if (!(parent.myType() == NodeType.PARKOUR || parent.myType() == NodeType.BRIDGE_AND_PARKOUR)) { //TODO || parent.myType() == NodeType.DROP
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
            //TODO get blocks to parkour to or the drop location
        }
        list.removeAll(Collections.singleton(null));
        return list;
    }
}
