package me.ktechnet.openmineai.PathExecutor.NodeExecutors;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Helpers.ExecutionHelper;
import me.ktechnet.openmineai.Helpers.PlayerControl;
import me.ktechnet.openmineai.Main;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.BackpropagateCondition;
import me.ktechnet.openmineai.Models.Enums.ExecutionResult;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.INodeTypeExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import java.util.ArrayList;

public class StepUpNodeExecutor implements INodeTypeExecutor {
    private PlayerControl pc = new PlayerControl();
    private EntityPlayerSP player = Minecraft.getMinecraft().player;

    private boolean timedOut;

    @Override
    public ExecutionResult Execute(INode next, INode current, boolean verbose, boolean RTP) {
        int xOffset = next.pos().x - current.pos().x;
        int zOffset = next.pos().z - current.pos().z;
        ExecutionHelper ex = new ExecutionHelper();
        String cardinal = ex.GetCardinal(xOffset, zOffset);
        int rotation = ex.GetRotation(cardinal);
        if (verbose) ChatMessageHandler.SendMessage("Turning to face " + cardinal + " (" + rotation + ")");
        pc.HardSetFacing(rotation, -99);
        ex.Centre(cardinal);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        timedOut = true;
                    }
                },
                1000
        );
        new Thread(() -> {
            try {
                PlayerControl.Jump = true;
                Thread.sleep(200);
                PlayerControl.Jump = false;
            } catch (InterruptedException e) {
                Main.logger.error(e.getMessage());
            }
        }).start();
        PlayerControl.Sprint = false;
        PlayerControl.MoveForward = true;
        double maxDist = (Math.abs(xOffset)) > 0 && (Math.abs(zOffset) > 0)  ? 1.5 : 1;
        while (!next.pos().IsEqual(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ)) && !timedOut) {
            pc.HardSetFacing(rotation, -99);
            PlayerControl.Sprint = false;
            if (DistanceHelper.GetComponents(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ), next.pos()).h > maxDist && !RTP) {
                PlayerControl.MoveForward = false;
                PlayerControl.Jump = false;
                if (verbose) ChatMessageHandler.SendMessage("No longer on route, node return abort");
                return ExecutionResult.OFF_PATH;
            }
        }
        PlayerControl.Jump = false;
        PlayerControl.MoveForward = false;
        return ExecutionResult.OK;
    }
}
