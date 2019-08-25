package me.ktechnet.openmineai.Models.ConfigData;

import me.ktechnet.openmineai.Helpers.AdjacentBlocksHelper;
import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Helpers.NodeClass;
import me.ktechnet.openmineai.Main;
import me.ktechnet.openmineai.Models.Classes.DistComponent;
import me.ktechnet.openmineai.Models.Classes.Node;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.NodeType;

import java.util.HashMap;

public class CostResolve {
    public static double Resolve(NodeType type, Pos pos, Pos dest) {
        return DynamicResolve(type, pos, dest); //Im lazy and cant be bothered to change all calls to this to dynamic
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

    private static double DynamicResolve(NodeType type, Pos pos, Pos dest) {
        DistComponent comp = DistanceHelper.GetComponents(pos, dest);
        double nClass = ClassResolve(type);
        double percentageVertical = GetPercent(comp.v, comp.n);
        double percentageHorizontal = GetPercent(comp.h, comp.n);
        int gravityBlocks = AdjacentBlocksHelper.GravityBlocksAbove(pos);
        boolean tooClose = false;
        if (Math.abs(percentageHorizontal - percentageVertical) <= 0.05 || comp.n < 10) tooClose = true;
        if (NodeClass.gravityCheck.contains(type)) nClass = nClass + (gravityBlocks);
        if (percentageHorizontal > percentageVertical) { //Bias to h
            if (NodeClass.horizontal.contains(type) && !tooClose) {
                return comp.n + (nClass - 0.4);
            } else {
                return comp.n + nClass;
            }
        } else { //Bias to v
            if (NodeClass.vertical.contains(type) && !tooClose) {
                return comp.n + (nClass - 1);
            } else {
                return comp.n + nClass;
            }
        }

    }
    private static double ClassResolve(NodeType type) {
        if (NodeClass.class1.contains(type)) return 1;
        if (NodeClass.class2.contains(type)) return 1.5;
        if (NodeClass.class3.contains(type)) return 2;
        return 4;
    }
    private static double GetPercent(double a, double b) {
        if (b == 0) return 0;
        return (a / b);
    }
}
