package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IOption;
import me.ktechnet.openmineai.Models.Interfaces.IOptionProvider;

import java.util.ArrayList;

public class OptionProvider implements IOptionProvider {
    private INode parent;

    public OptionProvider(INode parent) {
        this.parent = parent;
    }

    @Override
    public INode parent() {
        return parent;
    }

    @Override
    public IOption EvaluatePosition(Pos position) {
        //TODO determine node type and cost
        return null;
    }

    @Override
    public ArrayList<IOption> EvaluateOptions() {
        //TODO get nearby locations and push to EvaluatePosition(Pos)
        return null;
    }
}
