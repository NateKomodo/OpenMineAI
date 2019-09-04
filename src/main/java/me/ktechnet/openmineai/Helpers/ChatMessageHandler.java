package me.ktechnet.openmineai.Helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class ChatMessageHandler {
    public static void SendMessage(String msg)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[OpenMineAI]" + TextFormatting.WHITE + " " + msg));
    }
}
