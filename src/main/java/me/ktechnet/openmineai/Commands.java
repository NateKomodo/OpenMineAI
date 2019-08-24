package me.ktechnet.openmineai;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.PlayerControl;
import me.ktechnet.openmineai.Models.Classes.PopulousBadStarSearch;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.Settings;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IPathingCallback;
import me.ktechnet.openmineai.Models.Interfaces.IPathingProvider;
import me.ktechnet.openmineai.Models.Interfaces.IRoute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IClientCommand;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Commands extends CommandBase implements IClientCommand, IPathingCallback {

    LocalDateTime pre;

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message)
    {
        return true;
    }

    @Override
    public String getName()
    {
        return ";o";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        //TODO this
        if (args.length > 0)
        {
            if (args[0].equals("1"))
            {
                new PlayerControl().BreakBlockConcurrent();
            }
            else if (args[0].equals("2"))
            {
                new PlayerControl().PlaceBlock();
            }
            else if (args[0].equals("3"))
            {
                new PlayerControl().TakeControl();
                PlayerControl.MoveForward = true;
                PlayerControl.Jump = true;
                PlayerControl.Sprint = true;
            }
            else if (args[0].equals("4"))
            {
                PlayerControl.MoveForward = false;
                PlayerControl.Jump = false;
                PlayerControl.Sprint = false;
            }
            else if (args[0].equals("5") && args.length == 6)
            {
                IPathingProvider pathingProvider = new PopulousBadStarSearch();
                EntityPlayerSP p = Minecraft.getMinecraft().player;
                Pos pos = new Pos((int)p.posX, (int)p.posY, (int)p.posZ);
                Pos dest = new Pos(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                pre = LocalDateTime.now();
                Settings settings = new Settings();
                settings.allowBreak = Boolean.parseBoolean(args[4]);
                settings.allowPlace = Boolean.parseBoolean(args[5]);
                pathingProvider.StartPathfinding(dest, pos, this, settings);
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void completeRouteFound(IRoute route) {
        LocalDateTime now = LocalDateTime.now();
        long diff = ChronoUnit.MILLIS.between(pre, now);
        ChatMessageHandler.SendMessage("Found complete route, took " + diff + "ms" + ". " + route.path().size() + " nodes");
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
    public void partialRouteFound(IRoute route) {
        LocalDateTime now = LocalDateTime.now();
        long diff = ChronoUnit.MILLIS.between(pre, now);
        ChatMessageHandler.SendMessage("Found partial route, took " + diff + "ms" + ". " + route.path().size() + " nodes");
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
    public void alternateRouteFound(IRoute route) {
        ChatMessageHandler.SendMessage("Found alternate route");
    }

    @Override
    public void outOfChunk(IRoute route) {
        ChatMessageHandler.SendMessage("Out of chunk route found");
    }

    private RayTraceResult rayTrace(Pos start)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        Vec3d vecStart = new Vec3d(start.x, start.y, start.z);
        Vec3d vecLook = new Vec3d(0, -10, 0);
        return player.world.rayTraceBlocks(vecStart, vecLook, true, false, true);
    }
}
