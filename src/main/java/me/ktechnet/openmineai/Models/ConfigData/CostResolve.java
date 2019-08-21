package me.ktechnet.openmineai.Models.ConfigData;

import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Helpers.NodeClass;
import me.ktechnet.openmineai.Models.Classes.DistComponent;
import me.ktechnet.openmineai.Models.Classes.Node;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.NodeType;

import java.util.HashMap;

public class CostResolve {
    public static double Resolve(NodeType type, Pos pos, Pos dest) {
        return DynamicResolve(type, pos, dest);
        //double dist = DistanceHelper.CalcDistance(pos, dest);
        //switch (type) {
        //    case MOVE:
        //    case PLAYER:
        //    case DESCEND:
        //    case ASCEND:
        //    case STEP_UP:
        //    case STEP_DOWN:
        //    case DROP:
        //        return 1 + dist; //Basic movement
        //    case BRIDGE:
        //    case PARKOUR:
        //    case BRIDGE_AND_PARKOUR:
        //    case ASCEND_TOWER:
        //        return 2 + dist; //Advanced movement and quick actions
        //    case DESCEND_MINE:
        //    case ASCEND_BREAK_AND_TOWER:
        //    case BREAK:
        //        return 3 + dist; //Low profile 1 block breaking
        //    case STEP_UP_AND_BREAK:
        //    case STEP_DOWN_AND_BREAK:
        //    case BREAK_AND_MOVE:
        //        return 4 + dist; //Lengthy process of breaking 2 or more blocks
        //    default:
        //        return 99;
        //}
    }

    public static double DynamicResolve(NodeType type, Pos pos, Pos dest) {
        DistComponent comp = DistanceHelper.GetComponents(pos, dest);
        double nClass = ClassResolve(type);
        if (comp.h > comp.v) { //Bias to h
            if (NodeClass.horizontal.contains(type)) {
                return comp.n + (nClass / 2);
            } else {
                return comp.n + nClass;
            }
        } else { //Bias to v
            if (NodeClass.vertical.contains(type)) {
                return comp.n + (nClass / 2);
            } else {
                return comp.n + nClass;
            }
        }

    }
    private static double ClassResolve(NodeType type) {
        if (NodeClass.class1.contains(type)) return 1;
        if (NodeClass.class2.contains(type)) return 2;
        if (NodeClass.class3.contains(type)) return 3;
        return 4;
    }
}
