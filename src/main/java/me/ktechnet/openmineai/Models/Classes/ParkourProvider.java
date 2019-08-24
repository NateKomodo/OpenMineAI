package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Models.Interfaces.IParkourOption;
import me.ktechnet.openmineai.Models.Interfaces.IParkourProvider;

import java.util.ArrayList;

public class ParkourProvider implements IParkourProvider {
    @Override
    public ArrayList<IParkourOption> GetParkourLocations(Pos pos, Pos parent) {
        ArrayList<IParkourOption> parkourOptions = new ArrayList<>();
        int xOffset = pos.x - parent.x;
        int zOffset = pos.z - parent.z;
        for (int i = 1; i < 5; i++) {
            int newXoffset = xOffset * i;
            int newZoffset = zOffset * i;

        }
        return parkourOptions;
    }
}
