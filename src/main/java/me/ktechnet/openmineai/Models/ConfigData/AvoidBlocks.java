package me.ktechnet.openmineai.Models.ConfigData;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.ArrayList;

public class AvoidBlocks {
    public static ArrayList<Block> blocks = new ArrayList<Block>() {
        {
            add(Blocks.LAVA);
            add(Blocks.FLOWING_LAVA);
        }
    };
}
