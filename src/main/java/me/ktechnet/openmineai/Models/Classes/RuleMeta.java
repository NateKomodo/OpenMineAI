package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Models.Interfaces.IRule;

public class RuleMeta {
    public boolean CheckBreakableLavaAdj = false;
    public boolean RequireHeadSpace = false;
    public boolean Diagonal = false;
    public IRule diagonalTest = null;
    public boolean BreakRequired = false;
    public boolean PlaceRequired = false;
}
