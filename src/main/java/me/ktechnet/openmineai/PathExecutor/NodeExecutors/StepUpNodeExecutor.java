package me.ktechnet.openmineai.PathExecutor.NodeExecutors;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Helpers.ExecutionHelper;
import me.ktechnet.openmineai.Helpers.PlayerControl;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.INodeTypeExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class StepUpNodeExecutor implements INodeTypeExecutor {
    private PlayerControl pc = new PlayerControl();
    private EntityPlayerSP player = Minecraft.getMinecraft().player;

    @Override
    public boolean Execute(INode next, INode current, boolean verbose) {
        int xOffset = next.pos().x - current.pos().x;
        int zOffset = next.pos().z - current.pos().z;
        ExecutionHelper ex = new ExecutionHelper();
        String cardinal = ex.GetCardinal(xOffset, zOffset);
        int rotation = ex.GetRotation(cardinal);
        if (verbose) ChatMessageHandler.SendMessage("Turning to face " + cardinal + " (" + rotation + ")");
        pc.HardSetFacing(rotation, -99);
        ex.Centre(cardinal);
        PlayerControl.Jump = true;
        PlayerControl.Sprint = false;
        PlayerControl.MoveForward = true;
        while (!next.pos().IsEqual(new Pos((int)player.posX, (int)player.posY, (int)player.posZ))) {
            pc.HardSetFacing(rotation, -99);
            PlayerControl.Sprint = false;
            if (DistanceHelper.GetComponents(new Pos((int)player.posX, (int)player.posY, (int)player.posZ), next.pos()).h > 2) {
                PlayerControl.MoveForward = false;
                PlayerControl.Jump = false;
                ChatMessageHandler.SendMessage("No longer on route, abort");
                return false;
            }
        }
        PlayerControl.Jump = false;
        PlayerControl.MoveForward = false;
        return true;
    }
}
