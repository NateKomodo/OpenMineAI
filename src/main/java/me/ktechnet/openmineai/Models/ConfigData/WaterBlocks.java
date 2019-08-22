package me.ktechnet.openmineai.Models.ConfigData;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.ArrayList;

public class WaterBlocks {
    public static ArrayList<Block> blocks = new ArrayList<Block>() {
        {
            add(Blocks.WATER);
            add(Blocks.FLOWING_WATER);
        }
    };
}
