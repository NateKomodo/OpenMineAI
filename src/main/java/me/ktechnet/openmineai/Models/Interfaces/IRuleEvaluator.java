package me.ktechnet.openmineai.Models.Interfaces;

import me.ktechnet.openmineai.Models.Classes.Pos;

public interface IRuleEvaluator {
    boolean Evaluate(Pos pos, IRule rule);
}
