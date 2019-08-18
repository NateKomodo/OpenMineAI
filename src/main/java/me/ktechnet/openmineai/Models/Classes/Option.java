package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.IOption;

public class Option implements IOption {
    private int cost;

    private NodeType typeCandidate;

    private Pos position;

    @Override
    public int cost() {
        return cost;
    }

    @Override
    public NodeType typeCandidate() {
        return typeCandidate;
    }

    @Override
    public Pos position() {
        return position;
    }

    public Option(int cost, NodeType candidate, Pos position) {
        this.cost = cost;
        this.typeCandidate = candidate;
        this.position = position;
    }
}
