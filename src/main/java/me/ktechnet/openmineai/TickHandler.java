package me.ktechnet.openmineai;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.PlayerControl;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TickHandler {
    @SubscribeEvent
    public void handleClientTick(TickEvent.ClientTickEvent event){
        if (event.phase == TickEvent.Phase.END) {
            if (PlayerControl.Mine) {
                Minecraft mc = Minecraft.getMinecraft();
                RayTraceResult result = rayTrace(5);
                if (result == null) return;
                if (mc.playerController.onPlayerDamageBlock(result.getBlockPos(), result.sideHit))
                    mc.player.swingArm(EnumHand.MAIN_HAND);
            }
        }
    }
    private RayTraceResult rayTrace(int maxDist)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return null;
        Vec3d vec3d = player.getPositionEyes(1f);
        Vec3d vec3d1 = player.getLook(1f);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * maxDist, vec3d1.y * maxDist, vec3d1.z * maxDist);
        return player.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }
}
