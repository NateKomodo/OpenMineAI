package me.ktechnet.openmineai.Models.ConfigData;

import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.NodeType;

public class CostResolve {
    public static int Resolve(NodeType type, Pos pos, Pos dest) {
        int dist = (int)DistanceHelper.CalcDistance(pos, dest);
        switch (type) {
            case MOVE:
            case PLAYER:
            case DESCEND:
            case ASCEND:
                return 1 + dist;
            case STEP_UP:
            case STEP_DOWN:
                return 2 + dist;
            case BRIDGE:
            case PARKOUR:
            case BRIDGE_AND_PARKOUR:
            case ASCEND_TOWER:
                return 3 + dist;
            case BREAK:
            case DESCEND_MINE:
            case BREAK_AND_MOVE:
            case ASCEND_BREAK_AND_TOWER:
            case STEP_UP_AND_BREAK:
                return 4 + dist;
            default:
                return 99;
        }
    }
}
