package me.ktechnet.openmineai.Models.Classes;

import net.minecraft.util.math.BlockPos;

public class Pos {
    public final int x;
    public int y;
    public final int z;

    public Pos(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Pos(BlockPos from) {
        this.x = from.getX() < 0 ? (from.getX() + 1) : from.getX();
        this.y = from.getY();
        this.z = from.getZ() < 0 ? (from.getZ() + 1) : from.getZ();
    }

    public BlockPos ConvertToBlockPos() {
        return new BlockPos(x < 0 ? (x - 1) : x, y, z < 0 ? (z - 1) : z);
    }

    public boolean IsEqual(Pos pos) {
        return ((x == pos.x) && (y == pos.y) && (z == pos.z));
    }

    public boolean IsEqualYIndescrim(Pos pos ) {
        return ((x == pos.x) && (z == pos.z));
    }

    public String toString() {
        return x + "," + y + "," + z;
    }
}
