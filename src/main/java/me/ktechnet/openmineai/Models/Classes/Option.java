package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.IOption;

public class Option implements IOption {
    private double cost;

    private NodeType typeCandidate;

    private Pos position;

    private Pos artificalParent;

    @Override
    public double cost() {
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

    @Override
    public Pos artificalParent() {
        return artificalParent;
    }

    public Option(double cost, NodeType candidate, Pos position, Pos artificalParent) {
        this.cost = cost;
        this.typeCandidate = candidate;
        this.position = position;
        this.artificalParent = artificalParent;
    }
}
