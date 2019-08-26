package me.ktechnet.openmineai.Pathfinder;

import me.ktechnet.openmineai.Helpers.AdjacentBlocksHelper;
import me.ktechnet.openmineai.Helpers.NodeTypeRules;
import me.ktechnet.openmineai.Models.Classes.ParkourOption;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.CostResolve;
import me.ktechnet.openmineai.Models.ConfigData.PassableBlocks;
import me.ktechnet.openmineai.Models.ConfigData.Settings;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.IParkourOption;
import me.ktechnet.openmineai.Models.Interfaces.IParkourProvider;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;

import java.util.ArrayList;

public class ParkourProvider implements IParkourProvider {
    @Override
    public ArrayList<IParkourOption> GetParkourLocations(Pos pos, Pos parent, Pos dest, int maxFall) {
        ArrayList<IParkourOption> parkourOptions = new ArrayList<>();
        int xOffset = pos.x - parent.x;
        int zOffset = pos.z - parent.z;
        if (AdjacentBlocksHelper.Below(parent) == Blocks.WATER) return parkourOptions;
        RuleEvaluator rev = new RuleEvaluator(new ArrayList<>(), new ArrayList<>(), parent, new Settings());
        NodeTypeRules r = new NodeTypeRules();
        boolean diagonal = (xOffset != 0 && zOffset != 0);
        int max = diagonal ? 3 : 4;
        int negativeMod = 0;
        for (int y = 1; y > -(maxFall + 1); y--) {
            int heightBonus = negativeMod == 0 ? (int) Math.floor(Math.abs(y) / 2) : 0;
            heightBonus = Math.min(heightBonus, 4);
            boolean cantDoMore = false;
            for (int i = 1; i < ((max - (y == 1 ? 1 : 0)) + heightBonus) - negativeMod; i++) {
                int newXoffset = xOffset * i;
                int newZoffset = zOffset * i;
                Pos newPos = new Pos(pos.x + newXoffset, pos.y + y, pos.z + newZoffset);
                if (rev.Evaluate(newPos, r.GetMove(diagonal)) && !PassableBlocks.blocks.contains(AdjacentBlocksHelper.Below(newPos)) && !cantDoMore) {
                    parkourOptions.add(new ParkourOption(CostResolve.Resolve(NodeType.PARKOUR, newPos, dest), newPos));
                } else if (CheckBlocked(newPos, diagonal, new Pos(pos.x + (xOffset * (i - 1)), pos.y + y, pos.z + (zOffset * (i - 1))))) {
                    int dist = ((max + heightBonus) - negativeMod) - i;
                    if (dist > negativeMod) negativeMod = dist;
                    cantDoMore = true;
                }
            }
            if (negativeMod >= 4) break;
        }
        return parkourOptions;
    }
    private boolean CheckBlocked(Pos pos, boolean diagonal, Pos previous) {
        Block b1 = Minecraft.getMinecraft().world.getBlockState(pos.ConvertToBlockPos()).getBlock();
        Block b2 = Minecraft.getMinecraft().world.getBlockState(new Pos(pos.x, pos.y + 1, pos.z).ConvertToBlockPos()).getBlock();
        Block b3 = Minecraft.getMinecraft().world.getBlockState(new Pos(pos.x, pos.y + 2, pos.z).ConvertToBlockPos()).getBlock();
        if (!PassableBlocks.blocks.contains(b1) || !PassableBlocks.blocks.contains(b2) || !PassableBlocks.blocks.contains(b3)) return true;
        if (diagonal && previous != null) {
            boolean checkX = CheckBlocked(new Pos(pos.x - (pos.x - previous.x), pos.y, pos.z), false, null);
            boolean checkZ = CheckBlocked(new Pos(pos.x, pos.y, pos.z - (pos.z - previous.z)), false, null);
            return checkX || checkZ;
        }
        return false;
    }
}
