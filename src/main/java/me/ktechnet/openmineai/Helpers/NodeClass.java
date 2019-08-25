package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Models.Enums.NodeType;

import java.util.ArrayList;

public class NodeClass {
    public static ArrayList<NodeType> gravityCheck = new ArrayList<NodeType>() {
        {
            add(NodeType.STEP_DOWN_AND_BREAK);
            add(NodeType.STEP_UP_AND_BREAK);
            add(NodeType.BREAK_AND_MOVE);
        }
    };

    public static ArrayList<NodeType> vertical = new ArrayList<NodeType>() {
        {
            add(NodeType.DESCEND_MINE);
            add(NodeType.DESCEND);
            add(NodeType.ASCEND);
            add(NodeType.ASCEND_BREAK_AND_TOWER);
            add(NodeType.ASCEND_TOWER);
            add(NodeType.DROP);
            add(NodeType.STEP_DOWN);
            add(NodeType.STEP_DOWN_AND_BREAK);
            add(NodeType.STEP_UP);
            add(NodeType.STEP_UP_AND_BREAK);
            add(NodeType.PARKOUR);
        }
    };
    public static ArrayList<NodeType> horizontal = new ArrayList<NodeType>() {
        {
            add(NodeType.MOVE);
            add(NodeType.STEP_UP_AND_BREAK);
            add(NodeType.STEP_UP);
            add(NodeType.STEP_DOWN_AND_BREAK);
            add(NodeType.STEP_DOWN);
            add(NodeType.BRIDGE);
            add(NodeType.BREAK_AND_MOVE);
            add(NodeType.PARKOUR);
            add(NodeType.SWIM);
        }
    };
    public static ArrayList<NodeType> class1 = new ArrayList<NodeType>() {
        {
            add(NodeType.MOVE);
            add(NodeType.STEP_UP);
            add(NodeType.STEP_DOWN);
            add(NodeType.ASCEND);
            add(NodeType.DESCEND);
            add(NodeType.DROP);
            add(NodeType.PARKOUR);
            add(NodeType.PLAYER);
            add(NodeType.DESTINATION);
            add(NodeType.ASCEND_TOWER);
        }
    };
    public static ArrayList<NodeType> class2 = new ArrayList<NodeType>() {
        {
            add(NodeType.ASCEND_BREAK_AND_TOWER);
            add(NodeType.DESCEND_MINE);
            add(NodeType.SWIM);
        }
    };
    public static ArrayList<NodeType> class3 = new ArrayList<NodeType>() {
        {
            add(NodeType.BRIDGE);
            add(NodeType.STEP_UP_AND_BREAK);
            add(NodeType.STEP_DOWN_AND_BREAK);
            add(NodeType.BREAK);
            add(NodeType.BREAK_AND_MOVE);
        }
    };



    public static ArrayList<NodeType> strictclass1 = new ArrayList<NodeType>() {
        {
            add(NodeType.MOVE);
            add(NodeType.SWIM);
            add(NodeType.ASCEND);
            add(NodeType.DESCEND);
        }
    };
    public static ArrayList<NodeType> strictclass2 = new ArrayList<NodeType>() {
        {
            add(NodeType.ASCEND_TOWER);
            add(NodeType.DROP);
            add(NodeType.PARKOUR);
            add(NodeType.BRIDGE);
            add(NodeType.ASCEND_BREAK_AND_TOWER);
        }
    };
    public static ArrayList<NodeType> strictclass3 = new ArrayList<NodeType>() {
        {
            add(NodeType.STEP_UP_AND_BREAK);
            add(NodeType.STEP_DOWN_AND_BREAK);
            add(NodeType.BREAK);
            add(NodeType.BREAK_AND_MOVE);
        }
    };
    public static ArrayList<NodeType> strictclass4 = new ArrayList<NodeType>() {
        {

            add(NodeType.DESCEND_MINE);
            add(NodeType.STEP_UP);
            add(NodeType.STEP_DOWN);
        }
    };

    public static int GetStrictClass(NodeType type) {
        if (strictclass1.contains(type)) return 1;
        if (strictclass2.contains(type)) return 2;
        if (strictclass3.contains(type)) return 3;
        if (strictclass4.contains(type)) return 4;
        return 0;
    }
}
