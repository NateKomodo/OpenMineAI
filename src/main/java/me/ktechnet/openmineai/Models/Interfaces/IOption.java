package me.ktechnet.openmineai.Models.Interfaces;

import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.NodeType;

public interface IOption {
    int cost();

    NodeType typeCandidate();

    Pos position();
}
