package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Models.ConfigData.Settings;
import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IPathingCallback;
import me.ktechnet.openmineai.Models.Interfaces.IPathingProvider;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class PopulousBadStarSearch implements IPathingProvider {

    private ConcurrentHashMap<Pos, INode> nodes = new ConcurrentHashMap<>(); //Holds a list of ref's to each node, excluding initial

    LinkedList<INode> queue = new LinkedList<>();

    private INode initial;

    private Pos dest;

    private IPathingCallback callback;

    private boolean hasFoundRoute = false;

    private boolean failed = false;

    private Settings settings;

    @Override
    public ConcurrentHashMap<Pos, INode> nodeManifest() {
        return nodes;
    }

    @Override
    public Queue<INode> toProcess() {
        return queue;
    }

    @Override
    public INode initial() {
        return initial;
    }

    @Override
    public Pos destination() {
        return dest;
    }

    @Override
    public Settings settings() {
        return settings;
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public void StartPathfinding(Pos destination, Pos start, IPathingCallback callbackClass, Settings settings) { //TODO for executor, see if we can save on ASCEND_TOWER_BREAK, or shortcuts in general
        dest = destination;
        this.settings = settings;
        this.callback = callbackClass;
        initial = new Node(NodeType.PLAYER, this, null, 0,0, start, destination, (int)DistanceHelper.CalcDistance(start, destination) * 3, null, 0);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (!hasFoundRoute) {
                            callback.failedToFindPath();
                            Cleanup();
                        }
                    }
                },
                20000
        );
        initial.SpawnChildren();
    }

    @Override
    public void RouteFound(BackpropagateCondition condition, ArrayList<INode> path) {
        if (path != null) {
            Route route = new Route(path, condition);
            if (callback == null || failed) return;
            switch (condition) {
                case PARTIAL:
                    callback.partialRouteFound(route);
                    break;
                case COMPLETE:
                    if (!hasFoundRoute) {
                        callback.completeRouteFound(route);
                        hasFoundRoute = true;
                    } else {
                        callback.alternateRouteFound(route);
                    }
                    break;
                case OUT_OF_CHUNK:
                    callback.outOfChunk(route);
                    break;
            }
        }
        INode next = queue.poll();
        if (next != null) {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            next.SpawnChildren(); //Bypass recursion limit
                        }
                    },
                    0
            );
        } else ChatMessageHandler.SendMessage("Completed all potential routes");
    }

    @Override
    public void Cleanup() {
        failed = true;
        nodes.clear();
    }
}
