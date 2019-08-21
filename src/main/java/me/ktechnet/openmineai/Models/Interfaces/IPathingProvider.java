package me.ktechnet.openmineai.Models.Interfaces;

import com.sun.jna.ptr.ByReference;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.Settings;
import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;

import java.util.ArrayList;
import java.util.HashMap;

public interface IPathingProvider {
    HashMap<Pos, INode> nodeManifest(); //Holds a list of ref's to each node, excluding initial

    INode initial();

    Pos destination();

    Settings settings();

    void StartPathfinding(Pos destination, Pos start, IPathingCallback callbackclass, Settings settings);

    void RouteFound(BackpropagateCondition condition, ArrayList<INode> path);
}
