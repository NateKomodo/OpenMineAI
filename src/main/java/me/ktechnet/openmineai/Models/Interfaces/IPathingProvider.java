package me.ktechnet.openmineai.Models.Interfaces;

import com.sun.jna.ptr.ByReference;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;

import java.util.ArrayList;
import java.util.HashMap;

public interface IPathingProvider {
    HashMap<Pos, INode> nodeManifest(); //Holds a list of ref's to each node, excluding initial

    INode initial();

    void StartPathfinding(Pos destination, Pos start, IPathingCallback callbackclass);

    void RouteFound(BackpropagateCondition condition, ArrayList<INode> path);
}
