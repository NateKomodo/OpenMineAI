package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Main;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

@SuppressWarnings("WeakerAccess")
public class PlayerControl {

    public static boolean MoveForward;
    public static boolean MoveBack;
    public static boolean StrafeLeft;
    public static boolean StrafeRight;
    public static boolean Jump;
    public static boolean Sprint;
    public static boolean Sneak;

    public void TakeControl()
    {
        Minecraft.getMinecraft().player.movementInput = new PlayerMovement();
    }

    public void BreakBlock(boolean enforceRotation)
    {
        Minecraft mc = Minecraft.getMinecraft();
        RayTraceResult result = rayTrace(5);
        int rotation = (int)mc.player.rotationYaw;
        int pitch  = (int)mc.player.rotationPitch;
        try {
            int elapsed = 0;
            while (mc.world.getBlockState(result.getBlockPos()).getMaterial() != Material.AIR && elapsed < 15000) {
                if (enforceRotation) HardSetFacing(rotation, pitch);
                if (mc.playerController.onPlayerDamageBlock(result.getBlockPos(), result.sideHit)) mc.player.swingArm(EnumHand.MAIN_HAND);
                Thread.sleep(40);
                elapsed += 40;
            }
        }
        catch (Exception ex)
        {
            Main.logger.error(ex.getMessage());
        }
    }

    public void PlaceBlock()
    {
        Minecraft mc = Minecraft.getMinecraft();
        RayTraceResult result = rayTrace(5);
        BlockPos blockpos = result.getBlockPos().offset(result.sideHit);
        if (mc.playerController.processRightClickBlock(mc.player, mc.world, blockpos, result.sideHit, result.hitVec, EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS) mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    public void Interact()
    {
        Minecraft mc = Minecraft.getMinecraft();
        RayTraceResult result = rayTrace(5);
        if (mc.playerController.processRightClickBlock(mc.player, mc.world, result.getBlockPos(), result.sideHit, result.hitVec, EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS) mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    public RayTraceResult rayTrace(int maxDist)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        Vec3d vec3d = player.getPositionEyes(1f);
        Vec3d vec3d1 = player.getLook(1f);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * maxDist, vec3d1.y * maxDist, vec3d1.z * maxDist);
        return player.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }
    @Deprecated
    public void SetFacing(double rotation, double pitch, int force) {
        try {
            boolean flag = false;
            boolean rot = false;
            boolean pit = false;
            while (!flag) {
                double cfacing = Minecraft.getMinecraft().player.rotationYaw;
                double cpitch = Minecraft.getMinecraft().player.rotationPitch;
                cfacing = (cfacing - 90) % 360;
                if (cfacing < 0) {
                    cfacing += 360.0;
                }
                double arotation = (rotation - 90) % 360;
                if (arotation < 0) {
                    arotation += 360.0;
                }
                arotation = Math.floor(arotation);
                cfacing = Math.floor(cfacing);
                cpitch = Math.floor(cpitch);
                if (!rot) {
                    if (cfacing > (arotation)) {
                        Minecraft.getMinecraft().player.rotationYaw -= force;
                    } else if (cfacing < (arotation)) {
                        Minecraft.getMinecraft().player.rotationYaw += force;
                    } else {
                        rot = true;
                    }
                }
                if (!pit) {
                    if (cpitch < (pitch)) {
                        Minecraft.getMinecraft().player.rotationPitch += (force);
                    } else if (cpitch > (pitch)) {
                        Minecraft.getMinecraft().player.rotationPitch -= (force);
                    } else {
                        pit = true;
                    }
                }
                flag = rot && pit;
                Thread.sleep(2);
            }
        } catch (Exception ignored) {

        }
    }
    @Deprecated
    public void PushFacing(double rotationOffset, double pitchOffset) {
        try {
            boolean flag = false;
            boolean doneRot = false;
            boolean donePitch = false;
            while (!flag) {
                if (rotationOffset > 1) {
                    Minecraft.getMinecraft().player.rotationYaw--;
                    rotationOffset--;
                } else if (rotationOffset < -1) {
                    Minecraft.getMinecraft().player.rotationYaw++;
                    rotationOffset++;
                } else {
                    doneRot = true;
                }
                if (pitchOffset > 0) {
                    Minecraft.getMinecraft().player.rotationPitch--;
                    pitchOffset--;
                } else if (pitchOffset < 0) {
                    Minecraft.getMinecraft().player.rotationPitch++;
                    pitchOffset++;
                } else {
                    donePitch = true;
                }
                flag = doneRot & donePitch;
                Thread.sleep(2);
            }
        } catch (Exception ignored) {

        }
    }
    public void HardSetFacing(float rotation, float pitch) {
        if (pitch != -99) Minecraft.getMinecraft().player.rotationPitch = pitch;
        if (rotation != -999)Minecraft.getMinecraft().player.rotationYaw = rotation;
    }
}
