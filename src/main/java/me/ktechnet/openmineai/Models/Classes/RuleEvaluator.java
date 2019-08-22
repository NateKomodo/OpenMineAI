package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Helpers.AdjacentLavaHelper;
import me.ktechnet.openmineai.Main;
import me.ktechnet.openmineai.Models.ConfigData.AvoidBlocks;
import me.ktechnet.openmineai.Models.ConfigData.HazardBlocks;
import me.ktechnet.openmineai.Models.ConfigData.PassableBlocks;
import me.ktechnet.openmineai.Models.ConfigData.WaterBlocks;
import me.ktechnet.openmineai.Models.Enums.Rules;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IRule;
import me.ktechnet.openmineai.Models.Interfaces.IRuleEvaluator;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;

import java.util.ArrayList;
import java.util.Map;

public class RuleEvaluator implements IRuleEvaluator {
    private ArrayList<Pos> brokenBlocks;
    private ArrayList<Pos> placedBlocks;
    private Pos parent;

    public RuleEvaluator(ArrayList<Pos> brokenBlocks, ArrayList<Pos> placedBlocks, Pos parent) {
        this.brokenBlocks = brokenBlocks;
        this.placedBlocks = placedBlocks;
        this.parent = parent;
    }
    @Override
    public boolean Evaluate(Pos pos, IRule rule) {
        ArrayList<Block> pBlocks = PassableBlocks.blocks;
        ArrayList<Block> aBlocks = AvoidBlocks.blocks;
        ArrayList<Block> wBlocks = WaterBlocks.blocks;
        ArrayList<Block> hBlocks = HazardBlocks.blocks;
        if (rule.ruleMeta().RequireHeadSpace) {
            if (!pBlocks.contains(FetchBlock(new Pos(parent.x, parent.y + 2, parent.z)))) return false;
        }
        if (rule.ruleMeta().Diagonal) {
            //Ensure we can actually go diagonally
            Pos toCheckX = new Pos(pos.x - (pos.x - parent.x), pos.y, pos.z);
            Pos toCheckZ = new Pos(pos.x, pos.y, pos.z - (pos.z - parent.z));
            if (!Evaluate(toCheckX, rule.ruleMeta().diagonalTest) || !Evaluate(toCheckZ, rule.ruleMeta().diagonalTest)) return false;
        }
        for (Map.Entry entry : rule.ruleStack().entrySet()) {
            Pos inStack = new Pos(pos.x, pos.y + (int)entry.getKey(), pos.z);
            Block b = FetchBlock(inStack);
            switch ((Rules)entry.getValue()) {
                case ANY:
                    continue;
                case PASSABLE:
                    if (!pBlocks.contains(b) && !IsBroken(inStack) || IsPlaced(inStack)) return false;
                    continue;
                case IMPASSABLE:
                    if (pBlocks.contains(b) || IsBroken(inStack)) return false;
                    continue;
                case IMPASSABLE_NOT_LIQUID:
                    if (pBlocks.contains(b) || aBlocks.contains(b) || IsBroken(inStack) || wBlocks.contains(b) || hBlocks.contains(b)) return false;
                    continue;
                case ANY_NOT_LIQUID:
                    if (aBlocks.contains(b) || wBlocks.contains(b)) return false;
                    continue;
                case BREAKABLE:
                    if ((pBlocks.contains(b) || aBlocks.contains(b) || b == Blocks.BEDROCK || IsBroken(inStack) || wBlocks.contains(b)) && !IsPlaced(inStack)) return false;
                    if (rule.ruleMeta().CheckBreakableLavaAdj && AdjacentLavaHelper.Check(pos.ConvertToBlockPos())) return false;
                    continue;
                case BREAKABLE_OR_PASSABLE:
                    if ((aBlocks.contains(b) || b == Blocks.BEDROCK || wBlocks.contains(b)) && !IsPlaced(inStack) && !IsBroken(inStack)) return false; //|| IsBroken(inStack) <- not sure why that was there, keeping it here in case something breaks
                    if (!(pBlocks.contains(b)) && rule.ruleMeta().CheckBreakableLavaAdj && AdjacentLavaHelper.Check(pos.ConvertToBlockPos())) return false;
                    continue;
                case CLIMBABLE:
                    if ((b != Blocks.LADDER && b != Blocks.VINE) || IsBroken(inStack)) return false;
                    continue;
                case PASSABLE_OR_CLIMBABLE:
                    if ((!pBlocks.contains(b) && (b != Blocks.LADDER && b != Blocks.VINE)) || IsBroken(inStack)) return false;
                    continue;
                case LAVA:
                    if (!aBlocks.contains(b)) return false;
                    continue;
                case WATER:
                    if (!wBlocks.contains(b)) return false;
                    continue;
                case LIQUID:
                    if (!wBlocks.contains(b) && !aBlocks.contains(b)) return false;
                    continue;
                case WATER_OR_PASSABLE:
                    if (!wBlocks.contains(b) && !pBlocks.contains(b)) return false;
                    continue;
            }
        }
        return true;
    }

    private boolean IsBroken(Pos inStack) {
        for (Pos pos : brokenBlocks) {
            if (pos.IsEqual(inStack)) {
                return true;
            }
        }
        return false;
    }

    private boolean IsPlaced(Pos inStack) {
        for (Pos pos : placedBlocks) {
            if (pos.IsEqual(inStack)) {
                return true;
            }
        }
        return false;
    }

    private Block FetchBlock(Pos pos) {
        return Minecraft.getMinecraft().world.getBlockState(pos.ConvertToBlockPos()).getBlock();
    }
}
