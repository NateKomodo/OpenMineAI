package me.ktechnet.openmineai.CommandModules;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.Settings;
import me.ktechnet.openmineai.Models.Interfaces.*;
import me.ktechnet.openmineai.PathExecutor.PathExecutor;
import me.ktechnet.openmineai.Pathfinder.PopulousBadStarSearch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import java.util.TimerTask;

public class Go implements ICommandModule, IPathingCallback, IPathExecutionCallback {
    private boolean verbose = false;

    private IRoute favouring;

    private IPathExecutionCallback executionCallback = this;
    private IPathingCallback pathingCallback = this;

    @Override
    public void Run(String[] args) {
        if (args.length == 4) {
            ChatMessageHandler.SendMessage("Pathfinding...");
            this.verbose = Boolean.parseBoolean(args[3]);
            IPathingProvider pathingProvider = new PopulousBadStarSearch();
            EntityPlayerSP p = Minecraft.getMinecraft().player;
            Pos pos = new Pos((int)p.posX, (int)p.posY, (int)p.posZ);
            Pos dest = new Pos(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            Settings settings = new Settings();
            settings.verbose = verbose;
            settings.allowPlace = false;
            settings.allowBreak = false;
            settings.allowParkour = false;
            pathingProvider.StartPathfinding(dest, pos, pathingCallback, settings);
            new java.util.Timer().schedule(new TimerTask() { //Due to the speed of the algo, we are just going to wait instead of starting and potentially needing to turn around
                @Override
                public void run() {
                    ChatMessageHandler.SendMessage("Executing most favourable path...");
                    IPathExecutor pathExecutor = new PathExecutor();
                    pathExecutor.ExecutePath(favouring, executionCallback, verbose);
                }
            }, 1000);
        } else {
            ChatMessageHandler.SendMessage("Insufficient args");
        }
    }

    @Override
    public String GetArgs() {
        return ";o go x y z [verbose] - pathfind and go to a location";
    }

    @Override
    public void completeRouteFound(IRoute route) {
        favouring = route;
        if (verbose) ChatMessageHandler.SendMessage("Found complete route");
    }

    @Override
    public void partialRouteFound(IRoute route) {
    }

    @Override
    public void alternateRouteFound(IRoute route) {
        if (verbose) ChatMessageHandler.SendMessage("Found alternate route");
        favouring = route.cost() < favouring.cost() ? route : favouring;
    }

    @Override
    public void outOfChunk(IRoute route) {

    }

    @Override
    public void failedToFindPath() {
        ChatMessageHandler.SendMessage("Failed to find path!");
    }

    @Override
    public void pathExecutionFailed() {
        ChatMessageHandler.SendMessage("Failed to execute path!");
    }

    @Override
    public void pathExecutionSuccess() {
        ChatMessageHandler.SendMessage("Path execution finished!");
    }
}
