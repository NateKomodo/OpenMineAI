package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Models.Enums.Rules;
import me.ktechnet.openmineai.Models.Interfaces.IRule;

import java.util.HashMap;
import java.util.Map;

public class Rule implements IRule {
    private final Map<Integer, Rules> stack = new HashMap<>();

    private final RuleMeta meta = new RuleMeta();

    @Override
    public Map<Integer, Rules> ruleStack() {
        return stack;
    }

    @Override
    public RuleMeta ruleMeta() {
        return meta;
    }

    @Override
    public void PushToStack(Integer index, Rules rule) {
        stack.put(index, rule);
    }
}
