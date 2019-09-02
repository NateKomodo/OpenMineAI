package me.ktechnet.openmineai.PathExecutor.NodeExecutors;

import me.ktechnet.openmineai.Helpers.*;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.ExecutionResult;
import me.ktechnet.openmineai.Models.Enums.MoveDirection;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.INodeTypeExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class DescendMineNodeExecutor implements INodeTypeExecutor {
    private final PlayerControl pc = new PlayerControl();
    private final EntityPlayerSP player = Minecraft.getMinecraft().player;
    private final World world = Minecraft.getMinecraft().world;

    private boolean timedOut = false;

    @Override
    public ExecutionResult Execute(INode next, INode current, boolean verbose, boolean RTP, boolean shouldTurn, MoveDirection direction) throws InterruptedException {
        if (verbose) ChatMessageHandler.SendMessage("Facing downwards");
        pc.HardSetFacing(-999, 90);
        new java.util.Timer(true).schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        timedOut = true;
                    }
                },
                5000
        );
        pc.HardSetFacing(-999, 90);
        Pos bPos = new Pos(pc.rayTrace(2).getBlockPos());
        while (!(world.getBlockState(bPos.ConvertToBlockPos()).getBlock() == Blocks.AIR)) {
            new ToolHelper().SelectTool(world.getBlockState(bPos.ConvertToBlockPos()).getBlock());
            pc.HardSetFacing(-999, 90);
            pc.BreakBlockSync(true);
        }
        while (!next.pos().IsEqual(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ)) && !timedOut) {
            pc.HardSetFacing(-999, 90);
        }
        Thread.sleep(100); //Stop playing swinging
        return ExecutionResult.OK;
    }
}
