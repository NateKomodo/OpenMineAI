package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.NodeType;

import java.util.ArrayList;

public class BrokenBlocksHelper {
    public ArrayList<Pos> Broken(NodeType type, Pos pos) {
        ArrayList<Pos> list = new ArrayList<>();
        switch (type) {
            case BREAK_AND_MOVE:
                list.add(pos);
                list.add(new Pos(pos.x, pos.y + 1, pos.z));
                break;
            case ASCEND_BREAK_AND_TOWER:
                list.add(new Pos(pos.x, pos.y + 1, pos.z));
                break;
            case STEP_UP_AND_BREAK:
                list.add(new Pos(pos.x, pos.y + 1, pos.z));
                list.add(new Pos(pos.x, pos.y + 2, pos.z));
                break;
            case STEP_DOWN_AND_BREAK:
                list.add(new Pos(pos.x, pos.y + 1, pos.z));
                list.add(new Pos(pos.x, pos.y + 0, pos.z));
                list.add(new Pos(pos.x, pos.y - 1, pos.z));
                break;
            case DESCEND_MINE:
                list.add(pos);
                break;
        }
        return list;
    }
}
