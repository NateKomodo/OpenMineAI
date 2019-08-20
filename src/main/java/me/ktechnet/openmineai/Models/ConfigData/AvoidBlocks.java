package me.ktechnet.openmineai.Models.ConfigData;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.ArrayList;

public class AvoidBlocks {
    public ArrayList<Block> blocks = new ArrayList<>();
    public AvoidBlocks() {
        blocks.add(Blocks.LAVA);
        blocks.add(Blocks.FLOWING_LAVA);
    }
}
