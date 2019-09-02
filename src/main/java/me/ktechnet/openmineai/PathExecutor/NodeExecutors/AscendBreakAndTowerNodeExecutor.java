package me.ktechnet.openmineai.PathExecutor.NodeExecutors;

import me.ktechnet.openmineai.Helpers.AdjacentBlocksHelper;
import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.PlayerControl;
import me.ktechnet.openmineai.Helpers.ToolHelper;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Enums.ExecutionResult;
import me.ktechnet.openmineai.Models.Enums.MoveDirection;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.INodeTypeExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class AscendBreakAndTowerNodeExecutor implements INodeTypeExecutor {
    private final PlayerControl pc = new PlayerControl();
    private final EntityPlayerSP player = Minecraft.getMinecraft().player;
    private final World world = Minecraft.getMinecraft().world;

    private boolean timedOut = false;

    @Override
    public ExecutionResult Execute(INode next, INode current, boolean verbose, boolean RTP, boolean shouldTurn, MoveDirection direction) throws InterruptedException {
        if (verbose) ChatMessageHandler.SendMessage("Facing upwards");
        pc.HardSetFacing(-999, -90);
        new java.util.Timer(true).schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        timedOut = true;
                    }
                },
                5000
        );
        Pos bPos = new Pos(pc.rayTrace(1).getBlockPos());
        while (!(world.getBlockState(bPos.ConvertToBlockPos()).getBlock() == Blocks.AIR)) {
            new ToolHelper().SelectTool(world.getBlockState(bPos.ConvertToBlockPos()).getBlock());
            pc.HardSetFacing(-999, -90);
            pc.BreakBlock(true);
            if (AdjacentBlocksHelper.GravityBlocksAbove(bPos) > 0) Thread.sleep(400); //Gives gravity blocks a chance to fall
        }
        if (verbose) ChatMessageHandler.SendMessage("Facing downwards");
        pc.HardSetFacing(-999, 90);
        PlayerControl.Jump = true;
        Thread.sleep(300);
        new ToolHelper().SelectDisposable();
        pc.PlaceBlock();
        PlayerControl.Jump = false;
        Thread.sleep(200);
        return ExecutionResult.OK;
    }
}
