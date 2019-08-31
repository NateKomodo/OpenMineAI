package me.ktechnet.openmineai;

import me.ktechnet.openmineai.CommandModules.AlphaTest;
import me.ktechnet.openmineai.CommandModules.Go;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("WeakerAccess")
@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main
{
    static final String NAME = "Open Mine AI";
    static final String VERSION = "@VERSION@";
    static final String MODID = "openmineai";

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Commands.modules.put("test", new AlphaTest());
        Commands.modules.put("go", new Go());
        ClientCommandHandler.instance.registerCommand(new Commands());
    }
}

