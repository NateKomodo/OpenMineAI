package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Models.Classes.Rule;
import me.ktechnet.openmineai.Models.Enums.Rules;
import me.ktechnet.openmineai.Models.Interfaces.IRule;

public class NodeTypeRules {
    public IRule GetMove() {
        IRule rule = new Rule();
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.PASSABLE);
        rule.PushToStack(-1, Rules.IMPASSABLE_NOT_LAVA);
        return rule;
    }
    public IRule GetStepUp() {
        IRule rule = new Rule();
        rule.PushToStack(2, Rules.PASSABLE);
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.IMPASSABLE_NOT_LAVA);
        return rule;
    }
    public IRule GetStepUpAndBreak() { //TODO this may cause diagonal tunneling, testing needed
        IRule rule = new Rule();
        rule.ruleMeta().CheckBreakableLavaAdj = true;
        rule.PushToStack(2, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(1, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(0, Rules.IMPASSABLE_NOT_LAVA);
        return rule;
    }
    public IRule GetStepDown() {
        IRule rule = new Rule();
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.PASSABLE);
        rule.PushToStack(-1, Rules.PASSABLE);
        rule.PushToStack(-2, Rules.IMPASSABLE_NOT_LAVA);
        return rule;
    }
    public IRule GetStepDownAndBreak() { //TODO this may cause diagonal tunneling, testing needed
        IRule rule = new Rule();
        rule.ruleMeta().CheckBreakableLavaAdj = true;
        rule.PushToStack(1, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(0, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(-1, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(-2, Rules.IMPASSABLE_NOT_LAVA);
        return rule;
    }
    public IRule GetBreakAndMove() {
        IRule rule = new Rule();
        rule.ruleMeta().CheckBreakableLavaAdj = true;
        rule.PushToStack(1, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(0, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(-1, Rules.IMPASSABLE_NOT_LAVA);
        return rule;
    }
    public IRule GetDecentOrParkourOrBridge() {
        IRule rule = new Rule();
        rule.ruleMeta().CheckBreakableLavaAdj = true;
        rule.PushToStack(0, Rules.PASSABLE);
        rule.PushToStack(-1, Rules.PASSABLE);
        rule.PushToStack(-2, Rules.PASSABLE);
        return rule;
    }
    public IRule GetTower() {
        IRule rule = new Rule();
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.PASSABLE);
        return rule;
    }
    public IRule GetBreakAndTower() {
        IRule rule = new Rule();
        rule.ruleMeta().CheckBreakableLavaAdj = true;
        rule.PushToStack(1, Rules.BREAKABLE);
        rule.PushToStack(0, Rules.PASSABLE);
        return rule;
    }
    public IRule GetLadder() {
        IRule rule = new Rule();
        rule.PushToStack(0, Rules.CLIMBABLE);
        return rule;
    }
    public IRule GetRareDrop() {
        IRule rule = new Rule();
        rule.PushToStack(0, Rules.PASSABLE);
        return rule;
    }
    public IRule GetDescentMine() {
        IRule rule = new Rule();
        rule.ruleMeta().CheckBreakableLavaAdj = true;
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.BREAKABLE);
        return rule;
    }
}
