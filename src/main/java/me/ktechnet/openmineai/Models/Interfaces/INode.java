package me.ktechnet.openmineai.Models.Interfaces;

import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;
import me.ktechnet.openmineai.Models.Enums.NodeType;

import java.util.ArrayList;

public interface INode {
    NodeType myType();

    IPathingProvider master();

    INode parent();

    ArrayList<INode> children();

    ArrayList<IOption> options();

    int myCost();

    int costToMe();

    double distanceToGoal();

    void Backpropegate(BackpropagateCondition condition);
}
