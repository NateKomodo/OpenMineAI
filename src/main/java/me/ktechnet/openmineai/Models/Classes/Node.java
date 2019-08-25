package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Main;
import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IOption;
import me.ktechnet.openmineai.Models.Interfaces.IOptionProvider;
import me.ktechnet.openmineai.Models.Interfaces.IPathingProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;

import java.util.*;

public class Node implements INode {

    private NodeType myType;

    private IPathingProvider master;

    private INode parent;

    private ArrayList<INode> children = new ArrayList<>();

    private ArrayList<IOption> options = new ArrayList<>();

    private double costToMe;

    private double myCost;

    private int TTL;

    private Pos destination;

    private Pos myPos;

    private IOptionProvider optionProvider;

    private Node me = this;

    private Pos artificalParent;


    Node(NodeType type, IPathingProvider master, INode parent, double currentCost, double myCost, Pos myPos, Pos destination, int TTL, Pos artificalParent) { //TODO am i a replicant or not, fix adj to destination but solid block issue
        this.myType = type;
        this.master = master;
        this.parent = parent;
        this.costToMe = currentCost;
        this.myCost = myCost;
        this.myPos = myPos;
        this.destination = destination;
        this.TTL = TTL;
        this.artificalParent = artificalParent;
        master.nodeManifest().put(myPos, this);
        //TODO check for collisions, and partial backprop
        optionProvider = new OptionProvider(this);
        Main.logger.info("Node, proxim to dest: " + DistanceHelper.CalcDistance(myPos, master.destination()) + " TTL: " + TTL + " myPos: " + myPos.x + "," + myPos.y + "," + myPos.z+ " dest: " + destination.x + "," + destination.y + "," + destination.z + " Type: " + myType);
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
    public Pos pos() {
        return myPos;
    }

    @Override
    public double myCost() {
        return myCost;
    }

    @Override
    public double costToMe() {
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
    public Pos artificialParent() {
        return artificalParent;
    }

    @Override
    public void Backpropagate(BackpropagateCondition condition, ArrayList<INode> path) {
        if (condition != BackpropagateCondition.FAILED) {
            path.add(this);
            if (myType != NodeType.PLAYER) {
                parent.Backpropagate(condition, path);
            } else {
                master.RouteFound(condition, path);
            }
        } else {
            children = null;
            if (parent != null) parent.Backpropagate(condition, null);
        }
    }

    @Override
    public void SpawnChildren() {
        if (master.failed()) {
            Backpropagate(BackpropagateCondition.FAILED, null);
            return;
        }
        if (myPos.IsEqual(destination)) {
            Backpropagate(BackpropagateCondition.COMPLETE, new ArrayList<>());
            return;
        }
        if (TTL == 1) {
            ChatMessageHandler.SendMessage("TTL exceeded.");
            Backpropagate(BackpropagateCondition.PARTIAL, new ArrayList<>());
            return;
        }
        options = optionProvider.EvaluateOptions();
        if (options == null) {
            Backpropagate(BackpropagateCondition.OUT_OF_CHUNK, new ArrayList<>());
            return;
        }
        Collections.sort(options, Comparator.comparingDouble(IOption::cost));
        if (!(options.size() > 0)) {
            ChatMessageHandler.SendMessage("No options found.");
            Backpropagate(BackpropagateCondition.PARTIAL, new ArrayList<>());
            return;
        }
        if (options.get(0).cost() > options.get(options.size() - 1).cost()) Collections.reverse(options);
        for (IOption opt : options) {
            Main.logger.info("Candidate: " + opt.typeCandidate() + " Cost: " + opt.cost());
        }
        IOption option1 = options.get(0);
        if (options.size() == 2) { //TODO actually get around to implementing antpathing so we dont have to do this
            if (options.get(0).typeCandidate() == NodeType.ASCEND_TOWER && options.get(1).typeCandidate() == NodeType.DESCEND_MINE) option1 = options.get(1);
        }
        //IOption option2 = options.get(1); Note: make sure there are sufficient choices
        //IOption option3 = options.get(2);
        //Runnable runnable = new Runnable() {
        //    @Override
        //    public void run() {
        //        Node node = new Node(option2.typeCandidate(), master, me, costToMe, option2.cost(), option2.position(), destination, TTL - 1);
        //        children.add(node);
        //        node.SpawnChildren();
        //    }
        //};
        //new Thread(runnable).start(); //TODO Antpath
        //Runnable runnable2 = new Runnable() {
        //    @Override
        //    public void run() {
        //        Node node = new Node(option3.typeCandidate(), master, me, costToMe, option3.cost(), option3.position(), destination, TTL - 1);
        //        children.add(node);
        //        node.SpawnChildren();
        //    }
        //};
        //new Thread(runnable2).start();
        Node node = new Node(option1.typeCandidate(), master, me, costToMe + option1.cost(), option1.cost(), option1.position(), destination, TTL - 1, option1.artificalParent());
        children.add(node);
        node.SpawnChildren();
    }
}
