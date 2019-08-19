package me.ktechnet.openmineai.Models.Interfaces;

import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;
import me.ktechnet.openmineai.Models.Enums.NodeType;

import java.util.ArrayList;

public interface INode {
    NodeType myType();

    IPathingProvider master();

    INode parent();

    ArrayList<INode> children();

    ArrayList<IOption> options();

    Pos pos();

    int myCost();

    int costToMe();

    double distanceToGoal();

    int TTL();

    void Backpropagate(BackpropagateCondition condition, ArrayList<INode> path);

    void SpawnChildren();
}
