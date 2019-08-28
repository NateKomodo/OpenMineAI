package me.ktechnet.openmineai.PathExecutor.NodeExecutors;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Helpers.ExecutionHelper;
import me.ktechnet.openmineai.Helpers.PlayerControl;
import me.ktechnet.openmineai.Main;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.ExecutionResult;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.INodeTypeExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import java.util.TimerTask;

public class StepDownNodeExecutor implements INodeTypeExecutor {
    private PlayerControl pc = new PlayerControl();
    private EntityPlayerSP player = Minecraft.getMinecraft().player;

    private boolean timedOut = false;

    @Override
    public ExecutionResult Execute(INode next, INode current, boolean verbose, boolean RTP) throws InterruptedException {
        int xOffset = next.pos().x - current.pos().x;
        int zOffset = next.pos().z - current.pos().z;
        ExecutionHelper ex = new ExecutionHelper();
        String cardinal = ex.GetCardinal(xOffset, zOffset);
        int rotation = ex.GetRotation(cardinal);
        if (verbose) ChatMessageHandler.SendMessage("Turning to face " + cardinal + " (" + rotation + ")");
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
        PlayerControl.Sprint = false;
        PlayerControl.MoveForward = true;
        double maxDist = (Math.abs(xOffset)) > 0 && (Math.abs(zOffset) > 0)  ? 2.5 : 2;
        while (!next.pos().IsEqual(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ)) && !timedOut) {
            PlayerControl.Sprint = false;
            pc.HardSetFacing(rotation, -99);
            double dist = DistanceHelper.GetComponents(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ), next.pos()).h;
            if (new Pos((int)player.posX, (int)Math.ceil(player.posY) - 1, (int)player.posZ).IsEqual(next.pos())) {
                new java.util.Timer(true).schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                PlayerControl.MoveForward = false;
                            }
                        },
                        100
                );
                new java.util.Timer(true).schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                PlayerControl.MoveForward = true;
                            }
                        },
                        200
                );
            }
            if (dist > maxDist && !RTP) {
                PlayerControl.MoveForward = false;
                if (verbose) ChatMessageHandler.SendMessage("No longer on route, node return abort. Dist " + dist + " Max:" + maxDist);
                return ExecutionResult.OFF_PATH;
            }
        }
        PlayerControl.MoveForward = false;
        return ExecutionResult.OK;
    }
}
