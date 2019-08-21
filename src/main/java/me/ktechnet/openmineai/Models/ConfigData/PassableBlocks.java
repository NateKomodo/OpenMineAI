package me.ktechnet.openmineai.Models.ConfigData;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.ArrayList;

public class PassableBlocks {
    public ArrayList<Block> blocks = new ArrayList<>(); //TODO move to init and static
    public PassableBlocks() {
        blocks.add(Blocks.AIR);
        blocks.add(Blocks.TALLGRASS);
        blocks.add(Blocks.RED_FLOWER);
        blocks.add(Blocks.YELLOW_FLOWER);
        blocks.add(Blocks.DEADBUSH);
    }
}
