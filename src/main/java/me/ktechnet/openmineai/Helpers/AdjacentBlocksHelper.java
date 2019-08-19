package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Models.Classes.Pos;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class AdjacentBlocksHelper {
    public static Block Above(Pos pos) {
        return Minecraft.getMinecraft().world.getBlockState(new Pos(pos.x, pos.y + 1, pos.z).ConvertToBlockPos()).getBlock();
    }
    public static Block Below(Pos pos) {
        return Minecraft.getMinecraft().world.getBlockState(new Pos(pos.x, pos.y - 1, pos.z).ConvertToBlockPos()).getBlock();
    }
}
