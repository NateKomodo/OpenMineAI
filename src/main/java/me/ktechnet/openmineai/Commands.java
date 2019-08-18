package me.ktechnet.openmineai;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.IClientCommand;

public class Commands extends CommandBase implements IClientCommand {
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
        Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
}
