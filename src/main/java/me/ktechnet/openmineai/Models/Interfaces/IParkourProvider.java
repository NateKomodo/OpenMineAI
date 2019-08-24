package me.ktechnet.openmineai.Models.Interfaces;

import me.ktechnet.openmineai.Models.Classes.Pos;

import java.util.ArrayList;

public interface IParkourProvider {
    ArrayList<IParkourOption> GetParkourLocations(Pos initial, Pos parent);
}
