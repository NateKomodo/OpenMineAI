package me.ktechnet.openmineai.CommandModules;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.PlayerControl;
import me.ktechnet.openmineai.Main;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.Settings;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.*;
import me.ktechnet.openmineai.PathExecutor.PathExecutor;
import me.ktechnet.openmineai.Pathfinder.PopulousBadStarSearch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AlphaTest implements ICommandModule, IPathingCallback, IPathExecutionCallback {
    private LocalDateTime pre;

    private final ArrayList<IRoute> routes = new ArrayList<>();

    private IRoute initial;

    @Override
    public void Run(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("pathfind") && args.length == 6) {
                ChatMessageHandler.SendMessage("Starting pathing");
                IPathingProvider pathingProvider = new PopulousBadStarSearch();
                EntityPlayerSP p = Minecraft.getMinecraft().player;
                Pos pos = new Pos((int)p.posX, (int)p.posY, (int)p.posZ);
                Pos dest = new Pos(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                pre = LocalDateTime.now();
                Settings settings = new Settings();
                settings.allowBreak = Boolean.parseBoolean(args[4]);
                settings.allowPlace = Boolean.parseBoolean(args[5]);
                pathingProvider.StartPathfinding(dest, pos, this, settings);
            } else if (args[0].equals("plot")) {
                if (routes.size() > 0) {
                    routes.sort(Comparator.comparingDouble(IRoute::cost));
                    if (routes.get(0).cost() > routes.get(routes.size() - 1).cost()) Collections.reverse(routes);
                    ChatMessageHandler.SendMessage("Total routes: " + routes.size() + ", lowest cost: " + routes.get(0).cost() + " distance: " + routes.get(0).path().size());
                    ChatMessageHandler.SendMessage("IsInitial? " + routes.get(0).equals(initial));
                    SpawnRoute(routes.get(0));
                    ChatMessageHandler.SendMessage("Route spawned, Make sure to use clear.");
                } else {
                    ChatMessageHandler.SendMessage("Insufficient routes found. Please run pathfind first");
                }
            } else if (args[0].equals("follow") && args.length == 2) {
                if (routes.size() > 0) {
                    ChatMessageHandler.SendMessage("Executing path");
                    routes.sort(Comparator.comparingDouble(IRoute::cost));
                    if (routes.get(0).cost() > routes.get(routes.size() - 1).cost()) Collections.reverse(routes);
                    IPathExecutor pathExecutor = new PathExecutor();
                    pathExecutor.ExecutePath(routes.get(0), this, Boolean.parseBoolean(args[1]));
                } else {
                    ChatMessageHandler.SendMessage("Insufficient routes found. Please run pathfind first");
                }
            } else if (args[0].equals("clear")) {
                routes.clear();
                ChatMessageHandler.SendMessage("Cleared route list");
            } else if (args[0].equals("stop")) {
                PlayerControl.MoveForward = false;
                PlayerControl.StrafeRight = false;
                PlayerControl.StrafeLeft = false;
                PlayerControl.Sprint = false;
                PlayerControl.Jump = false;
                PlayerControl.MoveBack = false;
                PlayerControl.Sneak = false;
            } else {
                ChatMessageHandler.SendMessage("Invalid command. Use: ;o help for help");
            }
        } else {
            ChatMessageHandler.SendMessage("Insufficient arguments. Use: ;o help for help");
        }
    }

    @Override
    public String GetArgs() {
        return "[pathfind / plot] [pathfind: x] [pathfind: y] [pathfind: z] [pathfind: allowBreak (true/false)] [pathfind: allowPlace (true/false)] - Pathfind: Pathfind to a location. Plot: plot current lowest path";
    }

    @Override
    public void completeRouteFound(IRoute route) {
        LocalDateTime now = LocalDateTime.now();
        long diff = ChronoUnit.MILLIS.between(pre, now);
        ChatMessageHandler.SendMessage("Found complete route, took " + diff + "ms" + ". " + route.path().size() + " nodes");
        initial = route;
        routes.add(route);
    }

    @Override
    public void partialRouteFound(IRoute route) {
        LocalDateTime now = LocalDateTime.now();
        long diff = ChronoUnit.MILLIS.between(pre, now);
        ChatMessageHandler.SendMessage("Found partial route, took " + diff + "ms" + ". " + route.path().size() + " nodes");
        routes.add(route);
    }

    @Override
    public void alternateRouteFound(IRoute route) {
        LocalDateTime now = LocalDateTime.now();
        long diff = ChronoUnit.MILLIS.between(pre, now);
        ChatMessageHandler.SendMessage("Found alternate route, took " + diff + "ms" + ". " + route.path().size() + " nodes");
        routes.add(route);
    }

    @Override
    public void outOfChunk(IRoute route) {
        ChatMessageHandler.SendMessage("Out of chunk route found");
    }

    @Override
    public void failedToFindPath() {
        ChatMessageHandler.SendMessage("Failed to find path");
    }

    private void SpawnRoute(IRoute route) {
        for (INode node : route.path()) {
            if (node.pos().IsEqual(node.master().destination()) || node.myType() == NodeType.DESTINATION) {
                Minecraft.getMinecraft().world.setBlockState(node.pos().ConvertToBlockPos(), Blocks.EMERALD_BLOCK.getDefaultState());
                continue;
            }
            switch (node.myType()) {
                case PLAYER:
                    Minecraft.getMinecraft().world.setBlockState(node.pos().ConvertToBlockPos(), Blocks.REDSTONE_BLOCK.getDefaultState());
                    break;
                case MOVE:
                case ASCEND_TOWER:
                    Minecraft.getMinecraft().world.setBlockState(node.pos().ConvertToBlockPos(), Blocks.COBBLESTONE.getDefaultState());
                    break;
                case STEP_UP:
                    Minecraft.getMinecraft().world.setBlockState(node.pos().ConvertToBlockPos(), Blocks.LAPIS_BLOCK.getDefaultState());
                    break;
                case STEP_UP_AND_BREAK:
                case DESCEND_MINE:
                case ASCEND_BREAK_AND_TOWER:
                case BREAK_AND_MOVE:
                    Minecraft.getMinecraft().world.setBlockState(node.pos().ConvertToBlockPos(), Blocks.BRICK_BLOCK.getDefaultState());
                    break;
                case STEP_DOWN:
                    Minecraft.getMinecraft().world.setBlockState(node.pos().ConvertToBlockPos(), Blocks.YELLOW_GLAZED_TERRACOTTA.getDefaultState());
                    break;
                case DROP:
                    Minecraft.getMinecraft().world.setBlockState(node.pos().ConvertToBlockPos(), Blocks.PURPUR_BLOCK.getDefaultState());
                    break;
                case BRIDGE:
                    Minecraft.getMinecraft().world.setBlockState(node.pos().ConvertToBlockPos(), Blocks.BLACK_GLAZED_TERRACOTTA.getDefaultState());
                    break;
                case SWIM:
                    Minecraft.getMinecraft().world.setBlockState(node.pos().ConvertToBlockPos(), Blocks.BLUE_GLAZED_TERRACOTTA.getDefaultState());
                    break;
                case PARKOUR:
                    Minecraft.getMinecraft().world.setBlockState(node.pos().ConvertToBlockPos(), Blocks.RED_GLAZED_TERRACOTTA.getDefaultState());
                    break;
            }
        }
    }

    @Override
    public void pathExecutionFailed() {

    }

    @Override
    public void pathExecutionSuccess() {

    }
}
