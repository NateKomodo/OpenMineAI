package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Models.Interfaces.IParkourOption;

public class ParkourOption implements IParkourOption {
    private double cost;

    private Pos pos;

    public ParkourOption(double cost, Pos pos) {
        this.cost = cost;
        this.pos = pos;
    }

    @Override
    public double Cost() {
        return cost;
    }

    @Override
    public Pos pos() {
        return pos;
    }
}
