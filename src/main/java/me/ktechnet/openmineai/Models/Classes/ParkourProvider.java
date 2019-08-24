package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Helpers.AdjacentBlocksHelper;
import me.ktechnet.openmineai.Helpers.NodeTypeRules;
import me.ktechnet.openmineai.Main;
import me.ktechnet.openmineai.Models.ConfigData.CostResolve;
import me.ktechnet.openmineai.Models.ConfigData.PassableBlocks;
import me.ktechnet.openmineai.Models.ConfigData.Settings;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.IParkourOption;
import me.ktechnet.openmineai.Models.Interfaces.IParkourProvider;

import java.util.ArrayList;

public class ParkourProvider implements IParkourProvider {
    @Override
    public ArrayList<IParkourOption> GetParkourLocations(Pos pos, Pos parent, Pos dest) {
        ArrayList<IParkourOption> parkourOptions = new ArrayList<>();
        int xOffset = pos.x - parent.x;
        int zOffset = pos.z - parent.z;
        RuleEvaluator rev = new RuleEvaluator(new ArrayList<>(), new ArrayList<>(), parent, new Settings());
        NodeTypeRules r = new NodeTypeRules();
        boolean diagonal = (xOffset != 0 && zOffset != 0);
        int max = diagonal ? 3 : 4;
        int negativeMod = 0;
        for (int y = 1; y > -11; y--) {
            int heightBonus = (int) Math.floor(Math.abs(y) / 2);
            for (int i = 1; i < (max + heightBonus) - negativeMod; i++) {
                int newXoffset = xOffset * i;
                int newZoffset = zOffset * i;
                Pos newPos = new Pos(pos.x + newXoffset, pos.y + y, pos.z + newZoffset);
                if (rev.Evaluate(newPos, r.GetMove(diagonal)) && !PassableBlocks.blocks.contains(AdjacentBlocksHelper.Below(newPos))) {
                    parkourOptions.add(new ParkourOption(CostResolve.Resolve(NodeType.PARKOUR, newPos, dest), newPos));
                } else if (rev.Evaluate(newPos, r.GetParkourBlocked())) {
                    int dist = ((max + heightBonus) - negativeMod) - i;
                    if (dist > negativeMod) negativeMod = dist;
                    break;
                }
            }
            if (negativeMod >= 4) break;
        }
        return parkourOptions;
    }
}
