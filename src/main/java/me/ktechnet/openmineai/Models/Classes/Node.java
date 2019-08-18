package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IOption;
import me.ktechnet.openmineai.Models.Interfaces.IPathingProvider;

import java.util.*;

public class Node implements INode {

    private NodeType myType;

    private IPathingProvider master;

    private INode parent;

    private ArrayList<INode> children = new ArrayList<>();

    private ArrayList<IOption> options = new ArrayList<>();

    private int costToMe;

    private int myCost;

    private Pos destination;

    private Pos myPos;


    @Override
    public NodeType myType() {
        return myType;
    }

    @Override
    public IPathingProvider master() {
        return master;
    }

    @Override
    public INode parent() {
        return parent;
    }

    @Override
    public ArrayList<INode> children() {
        return children;
    }

    @Override
    public ArrayList<IOption> options() {
        return options;
    }

    @Override
    public int myCost() {
        return myCost;
    }

    @Override
    public int costToMe() {
        return costToMe;
    }

    @Override
    public double distanceToGoal() {
        return Math.sqrt(Math.pow(myPos.x - destination.x, 2) + Math.pow(myPos.y - destination.y, 2) + Math.pow(myPos.z - destination.z, 2));
    }

    @Override
    public void Backpropagate(BackpropagateCondition condition, ArrayList<INode> path) {
        if (myType != NodeType.PLAYER) {
            path.add(this);
            parent.Backpropagate(condition, path);
        } else {
            master.RouteFound(condition, path);
        }
    }

    Node(NodeType type, IPathingProvider master, INode parent, int currentCost, int myCost, Pos myPos, Pos destination) {
        this.myType = type;
        this.master = master;
        this.parent = parent;
        this.costToMe = currentCost;
        this.myCost = myCost;
        this.myPos = myPos;
        this.destination = destination;
        if (myPos == destination) {
            Backpropagate(BackpropagateCondition.COMPLETE, new ArrayList<>());
            return;
        }
        //TODO option and population logic
    }
}
