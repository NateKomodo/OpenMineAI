package me.ktechnet.openmineai.Pathfinder;

import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IRoute;

import java.util.ArrayList;
import java.util.Collections;

public class Route implements IRoute {
    private final ArrayList<INode> path;

    private final double cost;

    private final BackpropagateCondition type;


    @Override
    public ArrayList<INode> path() {
        return path;
    }

    @Override
    public double cost() {
        return cost;
    }

    @Override
    public BackpropagateCondition type() {
        return type;
    }

    public Route(ArrayList<INode> path, BackpropagateCondition condition) {
        Collections.reverse(path);
        this.path = path;
        this.type = condition;
        this.cost = path.stream().mapToDouble(INode::myCost).sum();
    }
}
