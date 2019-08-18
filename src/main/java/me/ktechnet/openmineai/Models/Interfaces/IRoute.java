package me.ktechnet.openmineai.Models.Interfaces;

import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;

import java.util.ArrayList;

public interface IRoute {
    ArrayList<INode> path();

    int cost();

    BackpropagateCondition type();
}
