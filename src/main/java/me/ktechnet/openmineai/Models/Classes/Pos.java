package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Main;
import net.minecraft.util.math.BlockPos;

public class Pos {
    public int x;
    public int y;
    public int z;

    public Pos(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos ConvertToBlockPos() {
        return new BlockPos(x, y, z);
    }

    public boolean IsEqual(Pos pos) {
        return ((x == pos.x) && (y == pos.y) && (z == pos.z));
    }

    public String toString() {
        return x + "," + y + "," + z;
    }
}
