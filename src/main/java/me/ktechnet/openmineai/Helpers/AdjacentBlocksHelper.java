package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Models.Classes.Pos;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;

public class AdjacentBlocksHelper {
    public static Block Above(Pos pos) {
        return Minecraft.getMinecraft().world.getBlockState(new Pos(pos.x, pos.y + 1, pos.z).ConvertToBlockPos()).getBlock();
    }
    public static Block Below(Pos pos) {
        return Minecraft.getMinecraft().world.getBlockState(new Pos(pos.x, pos.y - 1, pos.z).ConvertToBlockPos()).getBlock();
    }
    public static Integer GravityBlocksAbove(Pos pos) {
        int amount = 0;
        for (int i = 2; i < 7; i++) {
            Block block = Minecraft.getMinecraft().world.getBlockState(new Pos(pos.x, pos.y + i, pos.z).ConvertToBlockPos()).getBlock();
            if (block == Blocks.GRAVEL || block == Blocks.SAND) {
                amount++;
            } else {
                break;
            }
        }
        return amount;
    }
}
