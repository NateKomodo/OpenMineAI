package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.NodeType;

import java.util.ArrayList;

public class PlacedBlocksHelper {
    public ArrayList<Pos> Placed(NodeType type, Pos pos) {
        ArrayList<Pos> list = new ArrayList<>();
        switch (type) {
            case BRIDGE:
            case ASCEND_BREAK_AND_TOWER:
            case ASCEND_TOWER:
                list.add(new Pos(pos.x, pos.y - 1, pos.z));
                break;
        }
        return list;
    }
}
