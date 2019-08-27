package me.ktechnet.openmineai.Pathfinder;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Main;
import me.ktechnet.openmineai.Models.Classes.NodeClass;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IOption;
import me.ktechnet.openmineai.Models.Interfaces.IOptionProvider;
import me.ktechnet.openmineai.Models.Interfaces.IPathingProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Node implements INode {

    private final NodeType myType;

    private final IPathingProvider master;

    private INode parent;

    private final ArrayList<INode> children = new ArrayList<>();

    private ArrayList<IOption> options = new ArrayList<>();

    private final double costToMe;

    private final double myCost;

    private final int TTL;

    private final Pos destination;

    private final Pos myPos;

    private final IOptionProvider optionProvider;

    private final Node me = this;

    private final Pos artificalParent;

    private final int replicationCount;

    private boolean complete = false;


    Node(NodeType type, IPathingProvider master, INode parent, double currentCost, double myCost, Pos myPos, Pos destination, int TTL, Pos artificalParent, int replication) {
        this.myType = type;
        this.master = master;
        this.parent = parent;
        this.costToMe = currentCost;
        this.myCost = myCost;
        this.myPos = myPos;
        this.destination = destination;
        this.TTL = TTL;
        this.artificalParent = artificalParent;
        this.replicationCount = replication;
        ((PopulousBadStarSearch)master).nodes.put(myPos.toString(), me);
        optionProvider = new OptionProvider(this);
        if (master.settings().verbose) ChatMessageHandler.SendMessage("Node, proxim to dest: " + DistanceHelper.CalcDistance(myPos, master.destination()) + " TTL: " + TTL + " myPos: " + myPos.x + "," + myPos.y + "," + myPos.z+ " dest: " + destination.x + "," + destination.y + "," + destination.z + " Type: " + myType);
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
    public boolean PartOfCompletedChain() {
        return complete;
    }

    @Override
    public void UpdateParent(INode newParent) {
        parent = newParent;
    }

    @Override
    public void ForwardPropagate() {
        if (children.size() == 0) {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Backpropagate(BackpropagateCondition.COMPLETE, new ArrayList<>()); //Bypass recursion limit
                        }
                    },
                    0
            );
        } else {
            children.get(0).ForwardPropagate();
        }
    }

    @Override
    public void Backpropagate(BackpropagateCondition condition, ArrayList<INode> path) {
        if (condition != BackpropagateCondition.FAILED) {
            complete = true;
            path.add(this);
            if (myType != NodeType.PLAYER) {
                parent.Backpropagate(condition, path);
            } else {
                master.RouteFound(condition, path);
            }
        } else {
            children.clear();
            if (parent != null) parent.Backpropagate(condition, null);
        }
    }

    @Override
    public void SpawnChildren() {
        if (CheckForCollisions()) {
            master.RouteFound(BackpropagateCondition.FAILED, null);
            return;
        }
        if (master.failed()) {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Backpropagate(BackpropagateCondition.FAILED, null); //Bypass recursion limit
                        }
                    },
                    0
            );
            return;
        }
        if (myPos.IsEqual(destination)) {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Backpropagate(BackpropagateCondition.COMPLETE, new ArrayList<>()); //Bypass recursion limit
                        }
                    },
                    0
            );
            return;
        }
        if (TTL == 1) {
            if (master.settings().verbose) ChatMessageHandler.SendMessage("TTL exceeded.");
            master.RouteFound(BackpropagateCondition.FAILED, null);
            return;
        }
        options = optionProvider.EvaluateOptions();
        if (options == null) {
            if (master.settings().verbose) ChatMessageHandler.SendMessage("OUT OF CHUNK");
            Backpropagate(BackpropagateCondition.OUT_OF_CHUNK, new ArrayList<>());
            return;
        }
        options.sort(Comparator.comparingDouble(IOption::cost));
        if (!(options.size() > 0)) {
            if (master.settings().verbose) ChatMessageHandler.SendMessage("No options found.");
            master.RouteFound(BackpropagateCondition.FAILED, null);
            return;
        }
        if (options.get(0).cost() > options.get(options.size() - 1).cost()) Collections.reverse(options);
        if (master.settings().verbose) for (int i = 0; i < Math.min(options.size(), 3); i++) {
            IOption o = options.get(i);
            ChatMessageHandler.SendMessage("Option: " + o.typeCandidate() + " Cost: " + o.cost());
        }

        IOption option1 = options.get(0);
        int PsClass = NodeClass.GetStrictClass(option1.typeCandidate());

        if (replicationCount < master.settings().maxReplication) {
            if (options.size() >= 2) {
                IOption option2 = options.get(1);
                int sClass = NodeClass.GetStrictClass(option2.typeCandidate());
                if (sClass != PsClass) {
                    Node node = new Node(option2.typeCandidate(), master, me, costToMe, option2.cost(), option2.position(), destination, TTL - 1, null, replicationCount + 1);
                    master.toProcess().add(node);
                }
            }
            if (options.size() >= 3) {
                IOption option3 = options.get(2);
                int sClass = NodeClass.GetStrictClass(option3.typeCandidate());
                if (sClass != PsClass) {
                    Node node = new Node(option3.typeCandidate(), master, me, costToMe, option3.cost(), option3.position(), destination, TTL - 1, null, replicationCount + 1);
                    master.toProcess().add(node);
                }
            }
        }

        Node node = new Node(option1.typeCandidate(), master, me, costToMe + option1.cost(), option1.cost(), option1.position(), destination, TTL - 1, option1.artificalParent(), replicationCount);
        children.add(node);
        node.SpawnChildren();
    }

    private boolean CheckForCollisions() {
        if (master.nodeManifest().containsKey(myPos.toString())) {
            //Collision!
            INode collidedWith = ((PopulousBadStarSearch)master).nodes.get(myPos.toString());
            if (collidedWith.PartOfCompletedChain()) {
                if (collidedWith.costToMe() > costToMe) {
                    if (collidedWith.children().size() > 0) {
                        if (master.settings().verbose) ChatMessageHandler.SendMessage("Collision and was superior, stitching route");
                            INode child = collidedWith.children().get(0);
                        child.UpdateParent(me);
                        child.ForwardPropagate();
                    }
                }
                return true;
            }
        }
        return false;
    }
}
