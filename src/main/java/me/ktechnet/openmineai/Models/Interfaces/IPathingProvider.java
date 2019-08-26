package me.ktechnet.openmineai.Models.Interfaces;

import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.Settings;
import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;

import java.util.ArrayList;
import java.util.HashMap;

public interface IPathingProvider {
    HashMap<String, INode> nodeManifest(); //Holds a list of ref's to each node, excluding initial

    ArrayList<INode> toProcess(); //Queue for antpathing

    INode initial();

    Pos destination();

    Settings settings();

    boolean failed();

    void StartPathfinding(Pos destination, Pos start, IPathingCallback callbackclass, Settings settings);

    void RouteFound(BackpropagateCondition condition, ArrayList<INode> path);

    void Cleanup();

    void StopPathfinding();
}
