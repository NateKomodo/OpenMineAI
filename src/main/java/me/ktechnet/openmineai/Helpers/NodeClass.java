package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Models.Enums.NodeType;

import java.util.ArrayList;

public class NodeClass {
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
            add(NodeType.BRIDGE_AND_PARKOUR);
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
            add(NodeType.BRIDGE_AND_PARKOUR);
            add(NodeType.PARKOUR);
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
        }
    };
    public static ArrayList<NodeType> class2 = new ArrayList<NodeType>() {
        {
            add(NodeType.ASCEND_TOWER);
            add(NodeType.ASCEND_BREAK_AND_TOWER);
            add(NodeType.DESCEND_MINE);
            add(NodeType.BRIDGE);
            add(NodeType.BRIDGE_AND_PARKOUR);
        }
    };
    public static ArrayList<NodeType> class3 = new ArrayList<NodeType>() {
        {
            add(NodeType.STEP_UP_AND_BREAK);
            add(NodeType.STEP_DOWN_AND_BREAK);
            add(NodeType.BREAK);
            add(NodeType.BREAK_AND_MOVE);

        }
    };
}
