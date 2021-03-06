package me.ktechnet.openmineai.PathExecutor.NodeExecutors;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Helpers.ExecutionHelper;
import me.ktechnet.openmineai.Helpers.PlayerControl;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.PassableBlocks;
import me.ktechnet.openmineai.Models.Enums.ExecutionResult;
import me.ktechnet.openmineai.Models.Enums.MoveDirection;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.INodeTypeExecutor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;

public class MoveNodeExecutor implements INodeTypeExecutor {
    private final PlayerControl pc = new PlayerControl();
    private final EntityPlayerSP player = Minecraft.getMinecraft().player;

    private boolean timedOut = false;

    @Override
    public ExecutionResult Execute(INode next, INode current, boolean verbose, boolean RTP, boolean shouldTurn, MoveDirection direction) throws InterruptedException {
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
                1000
        );
        if (Minecraft.getMinecraft().world.getBlockState(new Pos(current.pos().x, current.pos().y -1, current.pos().z).ConvertToBlockPos()).getBlock() == Blocks.WATER) PlayerControl.Jump = true;
        ex.PushMovementState(true, direction, shouldTurn);
        PlayerControl.Sprint = true;
        Block b = Minecraft.getMinecraft().world.getBlockState(next.pos().ConvertToBlockPos()).getBlock();
        if (PassableBlocks.interactable.contains(b)) {
            if (PassableBlocks.doors.contains(b)) {
                if (!Minecraft.getMinecraft().world.getBlockState(next.pos().ConvertToBlockPos()).getValue(BlockDoor.OPEN)) {
                    pc.HardSetFacing(rotation, 50);
                    pc.Interact();
                }
            } else if (PassableBlocks.fencegates.contains(b)) {
                if (!Minecraft.getMinecraft().world.getBlockState(next.pos().ConvertToBlockPos()).getValue(BlockFenceGate.OPEN)) {
                    pc.HardSetFacing(rotation, 50);
                    pc.Interact();
                }
            }
        }
        double maxDist = (Math.abs(xOffset)) > 0 && (Math.abs(zOffset) > 0)  ? 1.6 : 1.1;
        while (!next.pos().IsEqual(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ)) && !timedOut) {
            pc.HardSetFacing(rotation, -99);
            PlayerControl.Sprint = true;
            double dist = DistanceHelper.GetComponents(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ), next.pos()).h;
            if (dist > maxDist && !RTP) {
                ex.PushMovementState(false, direction, shouldTurn);
                PlayerControl.Sprint = false;
                if (verbose) ChatMessageHandler.SendMessage("No longer on route, node return abort. Dist: " + dist + " Max: " + maxDist);
                return ExecutionResult.OFF_PATH;
            }
        }
        boolean wasSwim = PlayerControl.Jump;
        PlayerControl.Jump = false;
        PlayerControl.Sprint = false;
        if (wasSwim) Thread.sleep(100);
        ex.PushMovementState(false, direction, shouldTurn);
        return ExecutionResult.OK;
    }
}
