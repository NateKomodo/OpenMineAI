package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Models.Classes.Pos;

public class DistanceHelper {
    public static double CalcDistance(Pos pos1, Pos pos2) {
        double deltaX = pos1.x - pos2.x;
        double deltaY = pos1.y - pos2.y;
        double deltaZ = pos1.z - pos2.z;

        return Math.abs(Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ)));
    }
}
