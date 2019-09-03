package me.ktechnet.openmineai.CommandModules;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Helpers.ToolHelper;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.Settings;
import me.ktechnet.openmineai.Models.Interfaces.*;
import me.ktechnet.openmineai.PathExecutor.PathExecutor;
import me.ktechnet.openmineai.Pathfinder.PopulousBadStarSearch;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;

import java.util.ArrayList;
import java.util.TimerTask;

public class Mining implements ICommandModule, IPathingCallback, IPathExecutionCallback {
    private final boolean verbose = true;

    private IRoute favouring;

    private final IPathExecutionCallback executionCallback = this;
    private final IPathingCallback pathingCallback = this;

    private IPathingProvider pathingProvider;
    private IPathExecutor pathExecutor;

    private boolean ready = false;

    private boolean canPathfind = true;

    private boolean doMine = false;

    private Pos currentTarget;

    private ArrayList<Pos> unreachable = new ArrayList<>();

    private boolean isExecuting = false;

    @Override
    public void Run(String[] args) {
        if (args.length >= 1) {
            if (args[0].equals("start") && !doMine) {
                ChatMessageHandler.SendMessage("Starting to mine");
                doMine = true;
                StartMining();
            } else if (args[0].equals("stop") && doMine) {
                ChatMessageHandler.SendMessage("Stopping");
                doMine = false;
                StopMining();
            } else {
                ChatMessageHandler.SendMessage("Cannot set state to current state!");
            }
        } else {
            ChatMessageHandler.SendMessage("Insufficient args");
        }
    }

    private void CheckFound() {
        new java.util.Timer().schedule(new TimerTask() { //Due to the speed of the algo, we are just going to wait instead of starting and potentially needing to turn around
            @Override
            public void run() {
                if (ready) {
                    ChatMessageHandler.SendMessage("Executing most favourable path...");
                    if (favouring == null) {
                        ChatMessageHandler.SendMessage("Resetting: goal was unreachable");
                        unreachable.add(currentTarget);
                        StopMining();
                        StartMining();
                    } else {
                        pathExecutor = new PathExecutor();
                        pathExecutor.ExecutePath(favouring, executionCallback, verbose);
                        isExecuting = true;
                        canPathfind = true;
                    }
                } else {
                    ready = true;
                    CheckFound();
                }
            }
        }, 250);
    }

    private void StopMining() {
        if (pathingProvider != null) pathingProvider.StopPathfinding();
        if (pathExecutor != null) pathExecutor.Abort();
        pathingProvider = null;
        pathExecutor = null;
        currentTarget = null;
        favouring = null;
        ready = false;
        isExecuting = false;
    }

    private void StartMining() {
        ChatMessageHandler.SendMessage("Pathfinding...");
        pathingProvider = new PopulousBadStarSearch();
        EntityPlayerSP p = Minecraft.getMinecraft().player;
        Pos pos = new Pos((int)p.posX, (int)p.posY, (int)p.posZ);
        Pos dest = Find(Blocks.DIAMOND_ORE);
        currentTarget = dest;
        Settings settings = new Settings();
        settings.verbose = verbose;
        settings.allowPlace = new ToolHelper().HasDisposable();
        settings.allowBreak = new ToolHelper().HasPickAxe();
        settings.allowParkour = true;
        settings.hasWaterBucket = new ToolHelper().HasWaterbucket();
        settings.TTLMultiplier = 4;
        if (!settings.allowPlace) {
            ChatMessageHandler.SendMessage("No disposables detected, this may inhibit your ability to traverse caves");
        }
        if (!settings.allowBreak) {
            ChatMessageHandler.SendMessage("No pickaxe detected, this WILL inhibit your ability to mine, abort");
            doMine = false;
            StopMining();
            return;
        }
        if (!settings.hasWaterBucket) {
            ChatMessageHandler.SendMessage("No waterbucket detected, this may inhibit your ability to traverse caves");
        }
        if (dest == null) {
            ChatMessageHandler.SendMessage("Could not find any of the request block, abort");
            doMine = false;
            StopMining();
            return;
        }
        if (canPathfind) {
            pathingProvider.StartPathfinding(dest, pos, pathingCallback, settings);
            CheckFound();
        } else {
            ChatMessageHandler.SendMessage("Could not start pathfinding!");
        }
    }

    private Pos Find(Block b) {
        EntityPlayerSP p = Minecraft.getMinecraft().player;
        Pos best = null;
        double dist = 999;
        for (int x = (int)p.posX - 20; x < ((int)p.posX + 20); x++) {
            for (int z = (int)p.posZ - 20; z < ((int)p.posZ + 20); z++) {
                for (int y = 1; y < 16; y++) { //TODO raise for other ores, spiral to get closest
                    Pos pos = new Pos(x, y, z);
                    if (Minecraft.getMinecraft().world.getBlockState(pos.ConvertToBlockPos()).getBlock() == b) {
                        boolean flag = false;
                        for (Pos up : unreachable) {
                            if (up != null) {
                                if (DistanceHelper.CalcDistance(up, pos) < 3) flag = true;
                            } else ChatMessageHandler.SendMessage("Encountered unreachable as null! this should not happen!");
                        }
                        if (flag) {
                            ChatMessageHandler.SendMessage("Found diamonds but was in unreachable cluster!");
                            continue;
                        }
                        if (!unreachable.contains(b)) {
                            double newDist = DistanceHelper.CalcDistance(new Pos((int)p.posX, (int)p.posY, (int)p.posZ), pos);
                            if (newDist < dist) {
                                best = pos;
                                dist = newDist;
                            }
                        }
                    }
                }
            }
        }
        return best;
    }

    @Override
    public String GetArgs() {
        return ";o mine <start / stop> [ore] - Mine an ore";
    }

    @Override
    public void completeRouteFound(IRoute route) {
        favouring = route;
        if (verbose) ChatMessageHandler.SendMessage("Found complete route");
        ready = false;
    }

    @Override
    public void partialRouteFound(IRoute route) {
    }

    @Override
    public void alternateRouteFound(IRoute route) {
        if (verbose) ChatMessageHandler.SendMessage("Found alternate route");
        favouring = route.cost() < favouring.cost() ? route : favouring;
        ready = false;
    }

    @Override
    public void outOfChunk(IRoute route) {
        ChatMessageHandler.SendMessage("Destination out of chunk, abort");
        doMine = false;
        StopMining();
    }

    @Override
    public void failedToFindPath() {
        if (!isExecuting) {
            ChatMessageHandler.SendMessage("Failed to find path!");
            ChatMessageHandler.SendMessage("Please use start again to retry, target marked as unreachable");
            if (currentTarget != null) unreachable.add(new Pos(currentTarget.x, currentTarget.y, currentTarget.z));
            doMine = false;
            StopMining();
            canPathfind = false;
        }
    }

    @Override
    public void pathExecutionFailed() {
        isExecuting = false;
        ChatMessageHandler.SendMessage("Failed to execute path! Retrying...");
        StopMining();
        StartMining();
    }

    @Override
    public void pathExecutionSuccess() {
        isExecuting = false;
        ChatMessageHandler.SendMessage("Path execution finished!");
        if (doMine) {
            StopMining();
            StartMining();
        }
    }
}
