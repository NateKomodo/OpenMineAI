package me.ktechnet.openmineai.Models.Interfaces;

import me.ktechnet.openmineai.Models.Classes.Pos;

import java.util.ArrayList;

public interface IOptionProvider {
    INode parent();

    IOption EvaluatePosition(Pos position);

    ArrayList<IOption> EvaluateOptions();
}
