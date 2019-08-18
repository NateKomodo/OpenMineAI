package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IRoute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class Route implements IRoute {
    private ArrayList<INode> path;

    private int cost;

    private BackpropagateCondition type;


    @Override
    public ArrayList<INode> path() {
        return path;
    }

    @Override
    public int cost() {
        return cost;
    }

    @Override
    public BackpropagateCondition type() {
        return type;
    }

    Route(ArrayList<INode> path, BackpropagateCondition condition) {
        Collections.reverse(path);
        this.path = path;
        this.type = condition;
        this.cost = path.stream().mapToInt(INode::myCost).sum();
    }
}
