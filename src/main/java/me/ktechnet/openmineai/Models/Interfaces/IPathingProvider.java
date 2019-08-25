package me.ktechnet.openmineai.Models.Interfaces;

import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.Settings;
import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public interface IPathingProvider {
    ConcurrentHashMap<Pos, INode> nodeManifest(); //Holds a list of ref's to each node, excluding initial

    INode initial();

    Pos destination();

    Settings settings();

    boolean failed();

    void StartPathfinding(Pos destination, Pos start, IPathingCallback callbackclass, Settings settings);

    void RouteFound(BackpropagateCondition condition, ArrayList<INode> path);

    void Cleanup();
}
