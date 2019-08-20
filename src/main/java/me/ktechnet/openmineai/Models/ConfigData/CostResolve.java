package me.ktechnet.openmineai.Models.ConfigData;

import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.NodeType;

public class CostResolve {
    public static double Resolve(NodeType type, Pos pos, Pos dest) {
        double dist = DistanceHelper.CalcDistance(pos, dest);
        switch (type) {
            case MOVE:
            case PLAYER:
            case DESCEND:
            case ASCEND:
            case STEP_UP:
            case STEP_DOWN:
                return 1 + dist; //Basic movement
            case DROP:
            case BRIDGE:
            case PARKOUR:
            case BRIDGE_AND_PARKOUR:
            case ASCEND_TOWER:
                return 2 + dist; //Advanced movement and quick actions
            case DESCEND_MINE:
            case ASCEND_BREAK_AND_TOWER:
            case BREAK:
                return 3 + dist; //Low profile 1 block breaking
            case STEP_UP_AND_BREAK:
            case STEP_DOWN_AND_BREAK:
            case BREAK_AND_MOVE:
                return 4 + dist; //Lengthy process of breaking 2 or more blocks
            default:
                return 99;
        }
    }
}
