package me.ktechnet.openmineai.Models.Classes;

import me.ktechnet.openmineai.Helpers.AdjacentBlocksHelper;
import me.ktechnet.openmineai.Helpers.AdjacentLavaHelper;
import me.ktechnet.openmineai.Models.ConfigData.AvoidBlocks;
import me.ktechnet.openmineai.Models.ConfigData.CostResolve;
import me.ktechnet.openmineai.Models.ConfigData.PassableBlocks;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IOption;
import me.ktechnet.openmineai.Models.Interfaces.IOptionProvider;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;

import java.util.ArrayList;
import java.util.Collections;

public class OptionProviderLegacy implements IOptionProvider {
    private INode parent;

    public OptionProviderLegacy(INode parent) {
        this.parent = parent;
    }

    @Override
    public INode parent() {
        return parent;
    }

    @Override
    public IOption EvaluatePosition(Pos pos, boolean diagonal) { //TODO parkour handler, check chunk is loaded, diagonals, fix ascent, move all of this to a rule based system
        Pos parent = this.parent.pos();
        Pos entry = new Pos(parent.x, parent.y, parent.z);
        if (this.parent.parent() != null) {
            entry = this.parent.parent().pos();
        }
        Pos dest = this.parent.master().destination();
        if (pos.IsEqual(entry)) {
            return null;
        }
        Block b = Minecraft.getMinecraft().world.getBlockState(pos.ConvertToBlockPos()).getBlock();
        ArrayList<Block> pBlocks = new PassableBlocks().blocks;
        ArrayList<Block> aBlocks = new AvoidBlocks().blocks;
        if (pos.y - parent.y == 1) {
            //Ascend
            if (pBlocks.contains(b)) {
                return new Option(CostResolve.Resolve(NodeType.ASCEND_TOWER, pos, dest), NodeType.ASCEND_TOWER, pos);
            } else if (b != Blocks.LADDER && b != Blocks.BEDROCK && !AdjacentLavaHelper.Check(pos.ConvertToBlockPos())) {
                return new Option(CostResolve.Resolve(NodeType.ASCEND_BREAK_AND_TOWER, pos, dest), NodeType.ASCEND_BREAK_AND_TOWER, pos);
            } else if (b == Blocks.LADDER) {
                return new Option(CostResolve.Resolve(NodeType.ASCEND, pos, dest), NodeType.ASCEND, pos);
            }
        } else if (pos.y - parent.y == -1) {
            //Descend
            if (pBlocks.contains(b)) {
                return new Option(CostResolve.Resolve(NodeType.DESCEND, pos, dest), NodeType.DESCEND, pos);
            } else if (b != Blocks.LADDER && b != Blocks.BEDROCK && !AdjacentLavaHelper.Check(pos.ConvertToBlockPos())) {
                return new Option(CostResolve.Resolve(NodeType.DESCEND_MINE, pos, dest), NodeType.DESCEND_MINE, pos);
            } else if (b == Blocks.LADDER) {
                return new Option(CostResolve.Resolve(NodeType.DESCEND, pos, dest), NodeType.DESCEND, pos);
            }
        } else { //TODO swim
            //Side nodes
            if (pBlocks.contains(b) && !(pBlocks.contains(AdjacentBlocksHelper.Below(pos))) && pBlocks.contains(AdjacentBlocksHelper.Above(pos)) && !(aBlocks.contains(AdjacentBlocksHelper.Below(pos)))) {
                //Walk floor
                return new Option(CostResolve.Resolve(NodeType.MOVE, pos, dest), NodeType.MOVE, pos);
            } else if (!(pBlocks.contains(b)) && pBlocks.contains(AdjacentBlocksHelper.Above(pos)) && !(aBlocks.contains(AdjacentBlocksHelper.Above(pos))) && pBlocks.contains(AdjacentBlocksHelper.Above(new Pos(pos.x, pos.y + 1, pos.z)))) {
                //Step up
                pos.y++;
                return new Option(CostResolve.Resolve(NodeType.STEP_UP, pos, dest), NodeType.STEP_UP, pos);
            } else if (!(pBlocks.contains(b)) && pBlocks.contains(AdjacentBlocksHelper.Above(pos)) && !(pBlocks.contains(AdjacentBlocksHelper.Above(new Pos(pos.x, pos.y + 1, pos.z)))) && !(AdjacentLavaHelper.Check(new Pos(pos.x, pos.y + 2, pos.z).ConvertToBlockPos()))) {
                //Step up and break
                pos.y++;
                return new Option(CostResolve.Resolve(NodeType.STEP_UP_AND_BREAK, pos, dest), NodeType.STEP_UP_AND_BREAK, pos);
            }
            else if (pBlocks.contains(b) && pBlocks.contains(AdjacentBlocksHelper.Below(pos)) &&  !(aBlocks.contains(AdjacentBlocksHelper.Below(new Pos(pos.x, pos.y - 1, pos.z)))) && !(pBlocks.contains(AdjacentBlocksHelper.Below(new Pos(pos.x, pos.y - 1, pos.z))))) {
                //Step down
                pos.y--;
                return new Option(CostResolve.Resolve(NodeType.STEP_DOWN, pos, dest), NodeType.STEP_DOWN, pos);
            } else if (!(pBlocks.contains(b)) && !(pBlocks.contains(AdjacentBlocksHelper.Above(pos))) && !AdjacentLavaHelper.Check(pos.ConvertToBlockPos())) {
                //Break and move into
                return new Option(CostResolve.Resolve(NodeType.BREAK_AND_MOVE, pos, dest), NodeType.BREAK_AND_MOVE, pos);
            } else if (pBlocks.contains(b) && !(pBlocks.contains(AdjacentBlocksHelper.Above(pos))) && !AdjacentLavaHelper.Check(pos.ConvertToBlockPos())) { //TODO check we can actually break the block
                //Break and move into
                return new Option(CostResolve.Resolve(NodeType.BREAK_AND_MOVE, pos, dest), NodeType.BREAK_AND_MOVE, pos);
            } else if (pBlocks.contains(b) && pBlocks.contains(AdjacentBlocksHelper.Below(pos)) && pBlocks.contains(AdjacentBlocksHelper.Below(new Pos(pos.x, pos.y - 1, pos.z)))) {
                //TODO choose decent node or bridge
            }
        }
        return null;
    }

    @Override
    public ArrayList<IOption> EvaluateOptions() {
        //TODO get nearby locations and push to EvaluatePosition(Pos)
        Pos pos = parent.pos();
        ArrayList<IOption> list = new ArrayList<>();
        if (!(parent.myType() == NodeType.PARKOUR || parent.myType() == NodeType.BRIDGE_AND_PARKOUR)) {
            list.add(EvaluatePosition(new Pos(pos.x + 1, pos.y, pos.z), false));
            list.add(EvaluatePosition(new Pos(pos.x + -1, pos.y, pos.z), false));
            list.add(EvaluatePosition(new Pos(pos.x, pos.y + 1, pos.z), false));
            list.add(EvaluatePosition(new Pos(pos.x, pos.y - 1, pos.z), false));
            list.add(EvaluatePosition(new Pos(pos.x, pos.y, pos.z + 1), false));
            list.add(EvaluatePosition(new Pos(pos.x, pos.y, pos.z - 1), false));
        } else {
            //TODO some thing to get blocks to parkour to
        }
        list.removeAll(Collections.singleton(null));
        return list;
    }
}
