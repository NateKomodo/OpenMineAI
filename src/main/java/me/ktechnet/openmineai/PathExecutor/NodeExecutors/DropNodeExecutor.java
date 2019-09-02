package me.ktechnet.openmineai.PathExecutor.NodeExecutors;

import me.ktechnet.openmineai.Helpers.*;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.PassableBlocks;
import me.ktechnet.openmineai.Models.Enums.ExecutionResult;
import me.ktechnet.openmineai.Models.Enums.MoveDirection;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.INodeTypeExecutor;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.awt.event.InputEvent;

public class DropNodeExecutor implements INodeTypeExecutor {
    private final PlayerControl pc = new PlayerControl();
    private final EntityPlayerSP player = Minecraft.getMinecraft().player;

    private boolean timedOut = false;

    @Override
    public ExecutionResult Execute(INode next, INode current, boolean verbose, boolean RTP, boolean shouldTurn, MoveDirection direction) throws Exception { //TODO Water bucket drop
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
        PlayerControl.Sprint = false;
        ex.PushMovementState(true, direction, shouldTurn);
        double maxDist = (Math.abs(xOffset)) > 0 && (Math.abs(zOffset) > 0)  ? 1.6 : 1.1;
        Pos expected = GetBlockBeneath(next.pos());
        expected.y++;
        boolean waterbucket = next.pos().y - expected.y > 10;
        boolean hasStopped = false;
        boolean hasBucketed = false;
        int trigger = Math.max((next.pos().y - expected.y) / 2, 10);
        new java.util.Timer(true).schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        timedOut = true;
                    }
                },
                Math.max(3000, ((next.pos().y - expected.y) / 20) * 1000)
        );
        while (!expected.IsEqual(new Pos((int)player.posX, (int)player.posY, (int)player.posZ)) && !timedOut) {
            PlayerControl.Sprint = false;
            pc.HardSetFacing(rotation, -99);
            double dist = DistanceHelper.GetComponents(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ), expected).h;
            if (new Pos((int)player.posX, expected.y, (int)player.posZ).IsEqual(expected) && !hasStopped) {
                    Thread.sleep(100);
                    ex.PushMovementState(false, direction, shouldTurn);
                    hasStopped = true;
                    ex.PushMovementState(true, MoveDirection.BACK, false);
                    Thread.sleep(80);
                    ex.PushMovementState(false, MoveDirection.BACK, false);
            }
            if (waterbucket && !hasBucketed) {
                if ((player.posY - expected.y) < trigger) {
                    Robot robot = new Robot();
                    while (true) {
                        Block b = Minecraft.getMinecraft().world.getBlockState(expected.ConvertToBlockPos()).getBlock();
                        Block b2 = Minecraft.getMinecraft().world.getBlockState(new Pos(expected.x + xOffset, expected.y, expected.z + zOffset).ConvertToBlockPos()).getBlock();
                        if (b == Blocks.WATER || b == Blocks.FLOWING_WATER || b2 == Blocks.WATER || b2 == Blocks.FLOWING_WATER) break;
                        pc.HardSetFacing(-999, 90);
                        new ToolHelper().SelectWaterbucket();
                        robot.mousePress(InputEvent.BUTTON3_MASK);
                        Thread.sleep(10);
                        robot.mouseRelease(InputEvent.BUTTON3_MASK);
                    }
                    hasBucketed = true;
                    Thread.sleep(75);
                    pc.HardSetFacing(-999, 90);
                    robot.mousePress(InputEvent.BUTTON3_MASK);
                    Thread.sleep(20);
                    robot.mouseRelease(InputEvent.BUTTON3_MASK);
                }
            }
            if (dist > maxDist && !RTP) {
                ex.PushMovementState(false, direction, shouldTurn);
                if (verbose) ChatMessageHandler.SendMessage("No longer on route, node return abort. Dist " + dist + " Max:" + maxDist);
                return ExecutionResult.OFF_PATH;
            }
        }
        ex.PushMovementState(false, direction, shouldTurn);
        return ExecutionResult.OK;
    }
    private Pos GetBlockBeneath(Pos start)
    {
        for (int i = start.y; i >= 0; i--) {
            BlockPos bPos = new Pos(start.x, i, start.z).ConvertToBlockPos();
            Block b = Minecraft.getMinecraft().world.getBlockState(bPos).getBlock();
            if (!PassableBlocks.blocks.contains(b)) return new Pos(bPos);
        }
        return null;
    }
}
