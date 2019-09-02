package me.ktechnet.openmineai.PathExecutor.NodeExecutors;

import me.ktechnet.openmineai.Helpers.*;
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

public class BridgeNodeExecutor implements INodeTypeExecutor {
    private final PlayerControl pc = new PlayerControl();
    private final EntityPlayerSP player = Minecraft.getMinecraft().player;

    private boolean timedOut = false;

    @Override
    public ExecutionResult Execute(INode next, INode current, boolean verbose, boolean RTP, boolean shouldTurn, MoveDirection direction) throws InterruptedException {
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
                10000
        );
        ex.PushMovementState(true, direction, shouldTurn);
        PlayerControl.Sprint = false;
        double maxDist = (Math.abs(xOffset)) > 0 && (Math.abs(zOffset) > 0)  ? 1.6 : 1.1;
        boolean hasPlaced = false;
        boolean flag = false;
        while (!flag) {
            pc.HardSetFacing(rotation, -99);
            PlayerControl.Sprint = false;
            if (!current.pos().IsEqual(new Pos((int)player.posX, (int)player.posY, (int)player.posZ)) && !hasPlaced) {
                PlayerControl.Sneak = true;
                ex.PushMovementState(false, direction, shouldTurn);
                Thread.sleep(100);
                int newRotation = ex.GetRotation(ex.InvertCardinal(cardinal));
                if (verbose) ChatMessageHandler.SendMessage("Turning to face " + ex.InvertCardinal(cardinal) + " (" + newRotation + ")");
                pc.HardSetFacing(newRotation, 82);
                Thread.sleep(100);
                new ToolHelper().SelectDisposable();
                pc.PlaceBlock();
                Thread.sleep(100);
                pc.HardSetFacing(rotation, -99);
                PlayerControl.Sneak = false;
                hasPlaced = true;
            }
            double dist = DistanceHelper.GetComponents(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ), next.pos()).h;
            if (dist > maxDist && !RTP) {
                ex.PushMovementState(false, direction, shouldTurn);
                PlayerControl.Sprint = false;
                if (verbose) ChatMessageHandler.SendMessage("No longer on route, node return abort. Dist: " + dist + " Max: " + maxDist);
                return ExecutionResult.OFF_PATH;
            }
            flag = next.pos().IsEqual(new Pos((int)player.posX, (int)player.posY, (int)player.posZ)) && !timedOut;
        }
        if (!hasPlaced) {
            PlayerControl.Sneak = true;
            ex.PushMovementState(false, direction, shouldTurn);
            Thread.sleep(100);
            int newRotation = ex.GetRotation(ex.InvertCardinal(cardinal));
            if (verbose) ChatMessageHandler.SendMessage("Turning to face " + ex.InvertCardinal(cardinal) + " (" + newRotation + ")");
            pc.HardSetFacing(newRotation, 82);
            Thread.sleep(100);
            new ToolHelper().SelectDisposable();
            pc.PlaceBlock();
            Thread.sleep(100);
            pc.HardSetFacing(rotation, -99);
            PlayerControl.Sneak = false;
        }
        ex.PushMovementState(false, direction, shouldTurn);
        return ExecutionResult.OK;
    }
}
