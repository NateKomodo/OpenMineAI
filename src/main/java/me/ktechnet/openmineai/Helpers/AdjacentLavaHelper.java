package me.ktechnet.openmineai.Helpers;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AdjacentLavaHelper {
    public static boolean Check(BlockPos pos) {
        World world = Minecraft.getMinecraft().world;
        boolean u = world.getBlockState(pos.offset(EnumFacing.UP)).getMaterial() == Material.LAVA;
        boolean d = world.getBlockState(pos.offset(EnumFacing.DOWN)).getMaterial() == Material.LAVA;
        boolean n = world.getBlockState(pos.offset(EnumFacing.NORTH)).getMaterial() == Material.LAVA;
        boolean s = world.getBlockState(pos.offset(EnumFacing.SOUTH)).getMaterial() == Material.LAVA;
        boolean e = world.getBlockState(pos.offset(EnumFacing.EAST)).getMaterial() == Material.LAVA;
        boolean w = world.getBlockState(pos.offset(EnumFacing.WEST)).getMaterial() == Material.LAVA;
        return u || d || n || s || e || w;
    }
}
