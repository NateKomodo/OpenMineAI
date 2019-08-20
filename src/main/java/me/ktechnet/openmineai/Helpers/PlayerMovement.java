package me.ktechnet.openmineai.Helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovementInput;

public class PlayerMovement extends MovementInput {
    public void updatePlayerMoveState() {
        this.moveForward = 0;
        this.moveStrafe = 0;

        if (PlayerControl.MoveForward || Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
            this.moveForward++;
        }
        if (PlayerControl.MoveBack || Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()) {
            this.moveForward--;
        }
        if (PlayerControl.StrafeLeft || Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()) {
            this.moveStrafe++;
        }
        if (PlayerControl.StrafeRight || Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()) {
            this.moveStrafe--;
        }
        this.jump = PlayerControl.Jump || Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
        if (PlayerControl.Sneak || Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown()) {
            this.sneak = true;
            this.moveStrafe *= 0.3D;
            this.moveForward *= 0.3D;
        }
        else
        {
            this.sneak = false;
        }
        Minecraft.getMinecraft().player.setSprinting(PlayerControl.Sprint);
    }
}
