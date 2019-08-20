package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IPathingCallback;
import me.ktechnet.openmineai.Models.Interfaces.IPathingProvider;

import java.util.ArrayList;
import java.util.HashMap;

public class PopulousAStarSearch implements IPathingProvider {

    private HashMap<Pos, INode> nodes = new HashMap<>(); //Holds a list of ref's to each node, excluding initial

    private INode initial;

    private Pos dest;

    private IPathingCallback callback;

    private boolean hasFoundRoute = false;

    @Override
    public HashMap<Pos, INode> nodeManifest() {
        return nodes;
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
    public void StartPathfinding(Pos destination, Pos start, IPathingCallback callbackClass) { //TODO fix jankyness of it going all of the place
        dest = destination;
        this.callback = callbackClass;
        initial = new Node(NodeType.PLAYER, this, null, 0,0, start, destination, ((int)DistanceHelper.CalcDistance(start, destination) + 10) * 3);
        initial.SpawnChildren();
    }

    @Override
    public void RouteFound(BackpropagateCondition condition, ArrayList<INode> path) {
        Route route = new Route(path, condition);
        switch (condition)
        {
            case PARTIAL:
                callback.partialRouteFound(route);
            case COMPLETE:
                if (!hasFoundRoute) {
                    callback.completeRouteFound(route);
                    hasFoundRoute = true;
                }
                else {
                    callback.alternateRouteFound(route);
                }
        }
    }
}
