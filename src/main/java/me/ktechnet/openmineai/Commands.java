package me.ktechnet.openmineai;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Models.Interfaces.ICommandModule;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class Commands extends CommandBase implements IClientCommand {

    public static final HashMap<String, ICommandModule> modules = new HashMap<>();

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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length > 0) {
            if (modules.containsKey(args[0])) {
                ICommandModule cmd = modules.get(args[0]);
                ArrayList<String> newArgs = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
                String[] str = newArgs.toArray(new String[0]);
                cmd.Run(str);
            } else if (args[0].equals("help")) {
                ChatMessageHandler.SendMessage("Usage: ;o [module name] [args ...]");
                for (Map.Entry<String, ICommandModule> m : modules.entrySet()) {
                    ChatMessageHandler.SendMessage("Module: " + m.getKey() + ": ;o " + m.getKey() + " " + m.getValue().GetArgs());
                }
            }  else {
                ChatMessageHandler.SendMessage("Invalid command. Use: ;o help for help");
            }
        } else {
            ChatMessageHandler.SendMessage("Insufficient arguments. Use: ;o help for help");
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
}
