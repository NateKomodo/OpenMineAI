package me.ktechnet.openmineai.Pathfinder;

import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.Settings;
import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IPathingCallback;
import me.ktechnet.openmineai.Models.Interfaces.IPathingProvider;

import java.util.ArrayList;
import java.util.HashMap;

public class PopulousBadStarSearch implements IPathingProvider {

    //MEGA NOTE: Seeing as java HATES my Pos class, we are going to be using string instead, as x,y,z
    private final HashMap<String, INode> nodes = new HashMap<>(); //Holds a list of ref's to each node, excluding initial

    private final ArrayList<INode> queue = new ArrayList<>();

    private INode initial;

    private Pos dest;

    private IPathingCallback callback;

    private boolean hasFoundRoute = false;

    private boolean failed = false;

    private Settings settings;

    @Override
    public HashMap<String, INode> nodeManifest() {
        return nodes;
    }

    @Override
    public ArrayList<INode> toProcess() {
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
    public void StartPathfinding(Pos destination, Pos start, IPathingCallback callbackClass, Settings settings) {
        dest = destination;
        this.settings = settings;
        this.callback = callbackClass;
        initial = new Node(NodeType.PLAYER, this, null, 0,0, start, destination, (int)DistanceHelper.CalcDistance(start, destination) * settings.TTLMultiplier, null, 0);
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
                2000
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
        if (queue.size() > 0) {
        INode next = queue.get(queue.size() - 1); //Start from end in order to get better stitching
        queue.remove(next);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            next.SpawnChildren(); //Bypass recursion limit
                        }
                    },
                    0
            );
        }
    }

    @Override
    public void Cleanup() {
        failed = true;
        nodes.clear();
    }

    @Override
    public void StopPathfinding() {
        Cleanup();
    }
}
