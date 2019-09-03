package me.ktechnet.openmineai.PathExecutor.NodeExecutors;

import me.ktechnet.openmineai.Helpers.*;
import me.ktechnet.openmineai.Models.Classes.Option;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.ExecutionResult;
import me.ktechnet.openmineai.Models.Enums.MoveDirection;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.*;
import me.ktechnet.openmineai.Pathfinder.ParkourProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ParkourNodeExecutor implements INodeTypeExecutor {
    private final PlayerControl pc = new PlayerControl();
    private final EntityPlayerSP player = Minecraft.getMinecraft().player;

    private boolean timedOut = false;

    private final Pos dest;

    public ParkourNodeExecutor(Pos dest) {
        this.dest = dest;
    }

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
                5000
        );
        Pos expected;
        ArrayList<IParkourOption> parkourOptions = new ParkourProvider().GetParkourLocations(next.pos(), current.pos(), dest, 10);
        if (parkourOptions.size() > 0) {
            parkourOptions.sort(Comparator.comparingDouble(IParkourOption::Cost));
            if (parkourOptions.get(0).Cost() > parkourOptions.get(parkourOptions.size() - 1).Cost()) Collections.reverse(parkourOptions);
            IParkourOption prkO = parkourOptions.get(0);
            expected = new Pos(prkO.pos().x, prkO.pos().y, prkO.pos().z);
        } else {
            return ExecutionResult.FAILED;
        }
        ex.PushMovementState(true, direction, shouldTurn);
        PlayerControl.Sprint = true;
        Thread.sleep(100);
        PlayerControl.Jump = true;
        Thread.sleep(100);
        PlayerControl.Jump = false;
        double maxDist = (Math.abs(xOffset)) > 0 && (Math.abs(zOffset) > 0)  ? 15 : 10;
        boolean hasStopped = false;
        boolean flag = false;
        if (verbose) ChatMessageHandler.SendMessage("Jumping to: " + expected);
        while (!flag) {
            pc.HardSetFacing(rotation, -99);
            if (!current.pos().IsEqual(new Pos((int)player.posX, (int)player.posY, (int)player.posZ))) {

            }
            if (expected.IsEqualYIndescrim(new Pos((int)player.posX, (int)player.posY, (int)player.posZ)) && !hasStopped) {
                ex.PushMovementState(false, direction, shouldTurn);
                ex.PushMovementState(true, MoveDirection.BACK, false);
                Thread.sleep(50);
                ex.PushMovementState(false, MoveDirection.BACK, false);
                hasStopped = true;
            }
            double dist = DistanceHelper.GetComponents(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ), next.pos()).h;
            if (dist > maxDist && !RTP) {
                ex.PushMovementState(false, direction, shouldTurn);
                PlayerControl.Sprint = false;
                if (verbose) ChatMessageHandler.SendMessage("No longer on route, node return abort. Dist: " + dist + " Max: " + maxDist);
                return ExecutionResult.OFF_PATH;
            }
            flag = expected.IsEqual(new Pos((int)player.posX, (int)player.posY, (int)player.posZ)) || timedOut;
        }
        if (verbose) ChatMessageHandler.SendMessage("Arrived at: " + expected + ", pos reads as " + new Pos((int)player.posX, (int)player.posY, (int)player.posZ));
        ex.PushMovementState(false, direction, shouldTurn);
        PlayerControl.Sprint = false;
        Thread.sleep(200); //Kill momentum by waiting
        return ExecutionResult.OK;
    }
}
