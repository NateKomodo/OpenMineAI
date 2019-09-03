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

public class StepUpAndBreakNodeExecutor implements INodeTypeExecutor {
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
        String cardinal = shouldTurn ? ex.GetCardinal(xOffset, zOffset) : ex.GetCardinalFromFacing();
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
        //Break block above
        pc.HardSetFacing(rotation, -90);
        Pos bPos = new Pos(pc.rayTrace(1).getBlockPos());
        while (!(world.getBlockState(bPos.ConvertToBlockPos()).getBlock() == Blocks.AIR)) {
            new ToolHelper().SelectTool(world.getBlockState(bPos.ConvertToBlockPos()).getBlock());
            pc.HardSetFacing(rotation, -90);
            pc.BreakBlock(true);
            if (AdjacentBlocksHelper.GravityBlocksAbove(bPos) > 0) Thread.sleep(400); //Gives gravity blocks a chance to fall
        }
        Thread.sleep(200);
        //Break block in front
        pc.HardSetFacing(rotation, 0);
        Pos bPos2 = new Pos(pc.rayTrace(2).getBlockPos());
        while (!(world.getBlockState(bPos2.ConvertToBlockPos()).getBlock() == Blocks.AIR)) {
            new ToolHelper().SelectTool(world.getBlockState(bPos2.ConvertToBlockPos()).getBlock());
            pc.HardSetFacing(rotation, 0);
            pc.BreakBlock(true);
            if (AdjacentBlocksHelper.GravityBlocksAbove(bPos2) > 0) Thread.sleep(400); //Gives gravity blocks a chance to fall
        }
        Thread.sleep(200);
        //Break block diagonal up
        pc.HardSetFacing(rotation, -50);
        Pos bPos3 = new Pos(pc.rayTrace(2).getBlockPos());
        while (!(world.getBlockState(bPos3.ConvertToBlockPos()).getBlock() == Blocks.AIR)) {
            new ToolHelper().SelectTool(world.getBlockState(bPos3.ConvertToBlockPos()).getBlock());
            pc.HardSetFacing(rotation, -50);
            pc.BreakBlock(true);
            if (AdjacentBlocksHelper.GravityBlocksAbove(bPos3) > 0) Thread.sleep(400); //Gives gravity blocks a chance to fall
        }
        //Enter new space
        PlayerControl.Sprint = false;
        PlayerControl.Jump = true;
        ex.PushMovementState(true, direction, shouldTurn);
        Thread.sleep(100);
        PlayerControl.Jump = false;
        double maxDist = (Math.abs(xOffset)) > 0 && (Math.abs(zOffset) > 0)  ? 1.6 : 1.1;
        while (!next.pos().IsEqual(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ)) && !timedOut) {
            pc.HardSetFacing(rotation, -99);
            double dist = DistanceHelper.GetComponents(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ), next.pos()).h;
            if (dist > maxDist && !RTP) {
                ex.PushMovementState(false, direction, shouldTurn);
                if (verbose) ChatMessageHandler.SendMessage("No longer on route, node return abort. Dist: " + dist + " Max: " + maxDist);
                return ExecutionResult.OFF_PATH;
            }
        }
        ex.PushMovementState(false, direction, shouldTurn);
        Thread.sleep(200);
        return ExecutionResult.OK;
    }
}
