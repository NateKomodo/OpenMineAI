package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Models.Classes.DistComponent;
import me.ktechnet.openmineai.Models.Classes.Pos;

import java.util.HashMap;

public class DistanceHelper {
    public static double CalcDistance(Pos pos1, Pos pos2) {
        double deltaX = pos1.x - pos2.x;
        double deltaY = pos1.y - pos2.y;
        double deltaZ = pos1.z - pos2.z;

        return Math.abs(Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ)));
    }
    public static DistComponent GetComponents(Pos pos1, Pos pos2) {
        DistComponent comp = new DistComponent();
        double deltaX = pos1.x - pos2.x;
        double deltaY = pos1.y - pos2.y;
        double deltaZ = pos1.z - pos2.z;

        comp.h = Math.abs(Math.sqrt((deltaX * deltaX) + (deltaZ * deltaZ)));
        comp.v = Math.abs(deltaY);
        comp.n = Math.abs(Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ)));

        return comp;
    }
}
