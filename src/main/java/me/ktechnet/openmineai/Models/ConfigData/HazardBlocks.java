package me.ktechnet.openmineai.Models.ConfigData;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.ArrayList;

public class HazardBlocks {
    public static ArrayList<Block> blocks = new ArrayList<Block>() {
        {
            add(Blocks.CACTUS);
            add(Blocks.MAGMA);
            add(Blocks.FIRE);
        }
    };
}
