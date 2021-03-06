package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Models.Classes.Rule;
import me.ktechnet.openmineai.Models.Enums.Rules;
import me.ktechnet.openmineai.Models.Interfaces.IRule;

public class NodeTypeRules {
    public IRule GetMove(boolean diagonal) {
        IRule rule = new Rule();
        rule.ruleMeta().Diagonal = diagonal;
        rule.ruleMeta().diagonalTest = GetDiagCheckMove();
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.PASSABLE);
        rule.PushToStack(-1, Rules.IMPASSABLE_NOT_LIQUID);
        return rule;
    }
    public IRule GetStepUp(boolean diagonal) {
        IRule rule = new Rule();
        rule.ruleMeta().Diagonal = diagonal;
        rule.ruleMeta().RequireHeadSpace = true;
        rule.ruleMeta().diagonalTest = GetDiagCheckStepUp();
        rule.PushToStack(2, Rules.PASSABLE);
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.IMPASSABLE_NOT_LIQUID);
        return rule;
    }
    public IRule GetStepUpAndBreak() {
        IRule rule = new Rule();
        rule.ruleMeta().CheckBreakableLavaAdj = true;
        rule.ruleMeta().BreakRequired = true;
        rule.PushToStack(2, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(1, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(0, Rules.IMPASSABLE_NOT_LIQUID);
        return rule;
    }
    public IRule GetStepDown(boolean diagonal) {
        IRule rule = new Rule();
        rule.ruleMeta().Diagonal = diagonal;
        rule.ruleMeta().diagonalTest = GetDiagCheckStepDown();
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.PASSABLE);
        rule.PushToStack(-1, Rules.PASSABLE);
        rule.PushToStack(-2, Rules.IMPASSABLE_NOT_LIQUID);
        return rule;
    }
    public IRule GetStepDownAndBreak() {
        IRule rule = new Rule();
        rule.ruleMeta().CheckBreakableLavaAdj = true;
        rule.ruleMeta().BreakRequired = true;
        rule.PushToStack(1, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(0, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(-1, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(-2, Rules.IMPASSABLE_NOT_LIQUID);
        return rule;
    }
    public IRule GetBreakAndMove() {
        IRule rule = new Rule();
        rule.ruleMeta().CheckBreakableLavaAdj = true;
        rule.ruleMeta().BreakRequired = true;
        rule.PushToStack(1, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(0, Rules.BREAKABLE_OR_PASSABLE);
        rule.PushToStack(-1, Rules.IMPASSABLE_NOT_LIQUID);
        return rule;
    }
    public IRule GetDecentOrParkourOrBridge(boolean diagonal) {
        IRule rule = new Rule();
        rule.ruleMeta().Diagonal = diagonal;
        rule.ruleMeta().diagonalTest = GetDiagCheckMove();
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.PASSABLE);
        rule.PushToStack(-1, Rules.PASSABLE);
        rule.PushToStack(-2, Rules.PASSABLE);
        return rule;
    }
    public IRule GetLiquidBridge() {
        IRule rule = new Rule();
        rule.ruleMeta().PlaceRequired = true;
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.PASSABLE);
        rule.PushToStack(-1, Rules.LIQUID);
        return rule;
    }
    public IRule GetTower() {
        IRule rule = new Rule();
        rule.ruleMeta().PlaceRequired = true;
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.PASSABLE);
        rule.PushToStack(-2, Rules.IMPASSABLE_NOT_LIQUID);
        return rule;
    }
    public IRule GetBreakAndTower() {
        IRule rule = new Rule();
        rule.ruleMeta().CheckBreakableLavaAdj = true;
        rule.ruleMeta().PlaceRequired = true;
        rule.ruleMeta().BreakRequired = true;
        rule.PushToStack(1, Rules.BREAKABLE);
        rule.PushToStack(0, Rules.PASSABLE);
        rule.PushToStack(-2, Rules.IMPASSABLE_NOT_LIQUID);
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
        rule.ruleMeta().BreakRequired = true;
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.BREAKABLE);
        return rule;
    }
    public IRule GetSwim(boolean diagonal) {
        IRule rule = new Rule();
        rule.ruleMeta().diagonalTest = GetDiagCheckSwim();
        rule.ruleMeta().Diagonal = diagonal;
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.PASSABLE);
        rule.PushToStack(-1, Rules.WATER);
        return rule;
    }
    private IRule GetDiagCheckSwim() {
        IRule rule = new Rule();
        rule.PushToStack(1, Rules.WATER_OR_PASSABLE);
        rule.PushToStack(0, Rules.WATER_OR_PASSABLE);
        rule.PushToStack(-1, Rules.WATER_OR_PASSABLE);
        return rule;
    }
    private IRule GetDiagCheckMove() {
        IRule rule = new Rule();
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.PASSABLE);
        return rule;
    }
    private IRule GetDiagCheckStepUp() {
        IRule rule = new Rule();
        rule.PushToStack(2, Rules.PASSABLE);
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.ANY);
        return rule;
    }
    private IRule GetDiagCheckStepDown() {
        IRule rule = new Rule();
        rule.PushToStack(1, Rules.PASSABLE);
        rule.PushToStack(0, Rules.PASSABLE);
        return rule;
    }
    public IRule GetParkourBlocked() {
        IRule rule = new Rule();
        rule.PushToStack(1, Rules.IMPASSABLE);
        rule.PushToStack(0, Rules.IMPASSABLE);
        return rule;
    }
}
