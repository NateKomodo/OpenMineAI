package me.ktechnet.openmineai.PathExecutor.NodeExecutors;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Helpers.ExecutionHelper;
import me.ktechnet.openmineai.Helpers.PlayerControl;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.PassableBlocks;
import me.ktechnet.openmineai.Models.Enums.ExecutionResult;
import me.ktechnet.openmineai.Models.Enums.MoveDirection;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.INodeTypeExecutor;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;

public class DropNodeExecutor implements INodeTypeExecutor {
    private final PlayerControl pc = new PlayerControl();
    private final EntityPlayerSP player = Minecraft.getMinecraft().player;

    private boolean timedOut = false;

    @Override
    public ExecutionResult Execute(INode next, INode current, boolean verbose, boolean RTP, boolean shouldTurn, MoveDirection direction) throws InterruptedException { //TODO Water bucket drop
        int xOffset = Integer.compare(next.pos().x - current.pos().x, 0);
        int zOffset = Integer.compare(next.pos().z - current.pos().z, 0);
        ExecutionHelper ex = new ExecutionHelper();
        String cardinal = shouldTurn ? ex.GetCardinal(xOffset, zOffset) : ex.GetCardinalFromFacing();
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
                1000
        );
        PlayerControl.Sprint = false;
        ex.PushMovementState(true, direction, shouldTurn);
        double maxDist = (Math.abs(xOffset)) > 0 && (Math.abs(zOffset) > 0)  ? 1.6 : 1.1;
        Pos expected = GetBlockBeneath(next.pos());
        expected = new Pos(expected.x, expected.y + 1, expected.z);
        while (!expected.IsEqual(new Pos((int)player.posX, (int)player.posY, (int)player.posZ)) && !timedOut) {
            PlayerControl.Sprint = false;
            pc.HardSetFacing(rotation, -99);
            double dist = DistanceHelper.GetComponents(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ), expected).h;
            if (new Pos((int)player.posX, expected.y, (int)player.posZ).IsEqual(expected)) {
                    Thread.sleep(100);
                    ex.PushMovementState(false, direction, shouldTurn);
            }
            if (dist > maxDist && !RTP) {
                ex.PushMovementState(false, direction, shouldTurn);
                if (verbose) ChatMessageHandler.SendMessage("No longer on route, node return abort. Dist " + dist + " Max:" + maxDist);
                return ExecutionResult.OFF_PATH;
            }
        }
        ex.PushMovementState(false, direction, shouldTurn);
        return ExecutionResult.OK;
    }
    private Pos GetBlockBeneath(Pos start)
    {
        for (int i = start.y; i >= 0; i--) {
            BlockPos bPos = new Pos(start.x, i, start.z).ConvertToBlockPos();
            Block b = Minecraft.getMinecraft().world.getBlockState(bPos).getBlock();
            if (!PassableBlocks.blocks.contains(b)) return new Pos(bPos);
        }
        return null;
    }
}
