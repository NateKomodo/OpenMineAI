package me.ktechnet.openmineai.Models.ConfigData;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.ArrayList;

public class PassableBlocks {
    public static ArrayList<Block> blocks = new ArrayList<Block>() {
        {
            add(Blocks.AIR);
            add(Blocks.TALLGRASS);
            add(Blocks.RED_FLOWER);
            add(Blocks.YELLOW_FLOWER);
            add(Blocks.DEADBUSH);
            add(Blocks.SNOW_LAYER);
            add(Blocks.RAIL);
            add(Blocks.ACTIVATOR_RAIL);
            add(Blocks.DETECTOR_RAIL);
            add(Blocks.GOLDEN_RAIL);
            add(Blocks.WATERLILY);
            add(Blocks.DOUBLE_PLANT);
        }
    };
}
