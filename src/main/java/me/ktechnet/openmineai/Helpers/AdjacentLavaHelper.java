package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Models.Classes.Pos;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class AdjacentLavaHelper {
    public static boolean Check(BlockPos pos) {
        World world = Minecraft.getMinecraft().world;
        Pos p = new Pos(pos);
        ArrayList<Block> blocks = new ArrayList<Block>() {
            {
                add(world.getBlockState(Get(p, 0, 1, 0)).getBlock());
                add(world.getBlockState(Get(p, 0, -1, 0)).getBlock());
                add(world.getBlockState(Get(p, 0, 0, -1)).getBlock());
                add(world.getBlockState(Get(p, 0, 0, 1)).getBlock());
                add(world.getBlockState(Get(p, 1, 0, 0)).getBlock());
                add(world.getBlockState(Get(p, -1, 0, 0)).getBlock());
            }
        };
        return blocks.contains(Blocks.LAVA) || blocks.contains(Blocks.FLOWING_LAVA);
    }
    private static BlockPos Get(Pos inital, int xOffset, int yOffset, int zOffset) {
        return new Pos(inital.x + xOffset, inital.y + yOffset, inital.z + zOffset).ConvertToBlockPos();
    }
}
