package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Main;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

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

    public void BreakBlockConcurrent()
    {
        Minecraft mc = Minecraft.getMinecraft();
        RayTraceResult result = rayTrace();
        Runnable runnable = () -> {
            try {
                int elapsed = 0;
                while (mc.world.getBlockState(result.getBlockPos()).getMaterial() != Material.AIR && elapsed < 15000) {
                    if (mc.playerController.onPlayerDamageBlock(result.getBlockPos(), result.sideHit))
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                    Thread.sleep(50);
                    elapsed += 50;
                }
                mc.playerController.resetBlockRemoving();
            }
            catch (Exception ex)
            {
                Main.logger.error(ex.getMessage());
            }
        };
        Thread t = new Thread(runnable);
        t.start();
    }

    public void BreakBlockSync()
    {
        Minecraft mc = Minecraft.getMinecraft();
        RayTraceResult result = rayTrace();
        try {
            int elapsed = 0;
            while (mc.world.getBlockState(result.getBlockPos()).getMaterial() != Material.AIR && elapsed < 15000) {
                if (mc.playerController.onPlayerDamageBlock(result.getBlockPos(), result.sideHit)) mc.player.swingArm(EnumHand.MAIN_HAND);
                Thread.sleep(50);
                elapsed += 50;
            }
            mc.playerController.resetBlockRemoving();
        }
        catch (Exception ex)
        {
            Main.logger.error(ex.getMessage());
        }
    }


    public void PlaceBlock()
    {
        Minecraft mc = Minecraft.getMinecraft();
        RayTraceResult result = rayTrace();
        BlockPos blockpos = result.getBlockPos().offset(result.sideHit);
        if (mc.playerController.processRightClickBlock(mc.player, mc.world, blockpos, result.sideHit, result.hitVec, EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS) mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    private RayTraceResult rayTrace()
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        Vec3d vec3d = player.getPositionEyes(1f);
        Vec3d vec3d1 = player.getLook(1f);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * 5, vec3d1.y * 5, vec3d1.z * 5);
        return player.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }
}
