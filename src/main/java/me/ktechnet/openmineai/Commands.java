package me.ktechnet.openmineai;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.PlayerControl;
import me.ktechnet.openmineai.Helpers.PlayerMovement;
import me.ktechnet.openmineai.Models.Classes.PopulousAStarSearch;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Interfaces.IPathingCallback;
import me.ktechnet.openmineai.Models.Interfaces.IPathingProvider;
import me.ktechnet.openmineai.Models.Interfaces.IRoute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
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
            else if (args[0].equals("5"))
            {
                IPathingProvider pathingProvider = new PopulousAStarSearch();
                EntityPlayerSP p = Minecraft.getMinecraft().player;
                Pos pos = new Pos((int)p.posX, (int)p.posY, (int)p.posZ);
                Pos dest = new Pos(pos.x + 500, pos.y, pos.z + 10);
                pre = LocalDateTime.now();
                pathingProvider.StartPathfinding(dest, pos, this);
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
        ChatMessageHandler.SendMessage("Found complete route, took " + diff + "ms");
    }

    @Override
    public void partialRouteFound(IRoute route) {
        ChatMessageHandler.SendMessage("Found partial route");
    }

    @Override
    public void alternateRouteFound(IRoute route) {
        ChatMessageHandler.SendMessage("Found alternate route");
    }
}
