package me.ktechnet.openmineai.Models.Interfaces;

import me.ktechnet.openmineai.Models.Classes.RuleMeta;
import me.ktechnet.openmineai.Models.Enums.Rules;

import java.util.Map;

public interface IRule {
    Map<Integer, Rules> ruleStack();

    RuleMeta ruleMeta();

    void PushToStack(Integer index, Rules rule);
}
