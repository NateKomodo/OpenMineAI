package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IOption;
import me.ktechnet.openmineai.Models.Interfaces.IOptionProvider;
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

    private int TTL;

    private Pos destination;

    private Pos myPos;

    private IOptionProvider optionProvider;

    private Node me = this;


    Node(NodeType type, IPathingProvider master, INode parent, int currentCost, int myCost, Pos myPos, Pos destination, int TTL) {
        this.myType = type;
        this.master = master;
        this.parent = parent;
        this.costToMe = currentCost;
        this.myCost = myCost;
        this.myPos = myPos;
        this.destination = destination;
        this.TTL = TTL;
        //TODO add to manifest, check for collisions, and partial backprop
        if (myPos == destination) {
            Backpropagate(BackpropagateCondition.COMPLETE, new ArrayList<>());
            return;
        }
        if (TTL == 1) {
            return;
        }
        optionProvider = new OptionProvider(this);
        SpawnChildren();
    }


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
        return DistanceHelper.CalcDistance(myPos, destination);
    }

    @Override
    public int TTL() {
        return TTL;
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

    @Override
    public void SpawnChildren() {
        options = optionProvider.EvaluateOptions();
        Collections.sort(options, new Comparator<IOption>() {
            @Override
            public int compare(IOption o1, IOption o2) {
                return Integer.compare(o1.cost(), o2.cost());
            }
        });
        if (!(options.size() > 0)) return;
        if (options.get(0).cost() > options.get(options.size() - 1).cost()) Collections.reverse(options);
        IOption option1 = options.get(0);
        IOption option2 = options.get(1);
        IOption option3 = options.get(2);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                new Node(option2.typeCandidate(), master, me, costToMe, option2.cost(), option2.position(), destination, TTL - 1);
            }
        };
        new Thread(runnable).start();
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                new Node(option3.typeCandidate(), master, me, costToMe, option3.cost(), option3.position(), destination, TTL - 1);
            }
        };
        new Thread(runnable2).start();
        new Node(option1.typeCandidate(), master, me, costToMe, option1.cost(), option1.position(), destination, TTL - 1);
    }
}
