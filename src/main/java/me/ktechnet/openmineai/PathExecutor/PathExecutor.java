package me.ktechnet.openmineai.PathExecutor;

import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IPathExecutor;
import me.ktechnet.openmineai.Models.Interfaces.IRoute;

public class PathExecutor implements IPathExecutor {
    @Override
    public void ExecutePath(IRoute route) {
        for (INode node : route.path());
    }
    private void ExecuteNode() { //TODO execute nodes (move into/etc) and see if we can shortcut/save

    }
}
