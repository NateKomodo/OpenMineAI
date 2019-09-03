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

public class BreakNodeExecutor implements INodeTypeExecutor {
    private final PlayerControl pc = new PlayerControl();
    private final EntityPlayerSP player = Minecraft.getMinecraft().player;
    private final World world = Minecraft.getMinecraft().world;

    private boolean timedOut = false;

    @Override
    public ExecutionResult Execute(INode next, INode current, boolean verbose, boolean RTP, boolean shouldTurn, MoveDirection direction) throws InterruptedException {
        shouldTurn = true;
        int xOffset = Integer.compare(next.pos().x - current.pos().x, 0);
        int zOffset = Integer.compare(next.pos().z - current.pos().z, 0);
        ExecutionHelper ex = new ExecutionHelper();
        String cardinal = ex.GetCardinal(xOffset, zOffset);
        int rotation = ex.GetRotation(cardinal);
        if (verbose) {
            if (shouldTurn) {
                ChatMessageHandler.SendMessage("Turning to face " + cardinal + " (" + rotation + ")");
            } else {
                ChatMessageHandler.SendMessage("Staying same facing and moving " + direction);
            }
        }
        pc.HardSetFacing(rotation, -99);
        ex.Centre(cardinal);
        new java.util.Timer(true).schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        timedOut = true;
                    }
                },
                5000
        );
        ex.PushMovementState(true, direction, shouldTurn);
        Thread.sleep(200);
        ex.PushMovementState(false, direction, shouldTurn);
        //Break bottom block
        pc.HardSetFacing(rotation, 70);
        Pos bPos = new Pos(pc.rayTrace(1).getBlockPos());
        while (!(world.getBlockState(bPos.ConvertToBlockPos()).getBlock() == Blocks.AIR)) {
            new ToolHelper().SelectTool(world.getBlockState(bPos.ConvertToBlockPos()).getBlock());
            pc.HardSetFacing(rotation, 70);
            pc.BreakBlock(true);
            if (AdjacentBlocksHelper.GravityBlocksAbove(bPos) > 0) Thread.sleep(400); //Gives gravity blocks a chance to fall
        }
        return ExecutionResult.OK;
    }
}
