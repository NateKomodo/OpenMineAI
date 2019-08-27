package me.ktechnet.openmineai.PathExecutor;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Helpers.PlayerControl;
import me.ktechnet.openmineai.Main;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IPathExecutionCallback;
import me.ktechnet.openmineai.Models.Interfaces.IPathExecutor;
import me.ktechnet.openmineai.Models.Interfaces.IRoute;
import me.ktechnet.openmineai.PathExecutor.NodeExecutors.MoveNodeExecutor;
import me.ktechnet.openmineai.PathExecutor.NodeExecutors.StepDownNodeExecutor;
import me.ktechnet.openmineai.PathExecutor.NodeExecutors.StepUpNodeExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;

public class PathExecutor implements IPathExecutor {
    private PlayerControl pc = new PlayerControl();
    private EntityPlayerSP player = Minecraft.getMinecraft().player;

    @Override
    public void ExecutePath(IRoute route, IPathExecutionCallback callback, boolean verbose) {
        pc.TakeControl();
        if (route.path().size() - 1 != 0) {
            new Thread(() -> {
                if (verbose) ChatMessageHandler.SendMessage("Starting execution");
                boolean complete = false;
                for (int i = 0; i < route.path().size() - 1; i++) {
                    if (!ExecuteNode(route.path().get(i + 1), route.path().get(i), verbose)) break;
                    int max = route.path().size() > 3 ? route.path().size() - 3 : 0;
                    if (i == (max)) complete = true;
                }
                if (complete) {
                    if (verbose) ChatMessageHandler.SendMessage("Path execution complete");
                    callback.pathExecutionSuccess();
                } else {
                    if (verbose) ChatMessageHandler.SendMessage("Path execution failed");
                    callback.pathExecutionFailed();
                }
            }).start();
        }
        else {
            if (verbose) ChatMessageHandler.SendMessage("Insufficient nodes");
            callback.pathExecutionSuccess();
        }
    }
    private boolean ExecuteNode(INode next, INode current, boolean verbose) { //TODO execute nodes (move into/etc) and see if we can shortcut/save, also see if we can return to trail if we get off it
        if (!current.pos().IsEqual(new Pos((int)player.posX, (int)player.posY, (int)player.posZ))) { //Check we are indeed at current
            if (verbose) ChatMessageHandler.SendMessage("No longer on route, abort");
            return false;
        }
        if (!ExecutionManager(next, current, verbose)) return false; //Execute, if failed return false
        if (!next.pos().IsEqual(new Pos((int)player.posX, (int)player.posY, (int)player.posZ))) { //Check we are now at end
            if (verbose) ChatMessageHandler.SendMessage("No longer on route, abort");
            return false;
        }
        return true; //Execution good and checks passed
    }
    private boolean ExecutionManager(INode next, INode current, boolean verbose) {
        try {
            switch (next.myType()) { //NOTE destination node is only spawned if both move and break_and_move cannot reach it and therefore needs a special use case, or just idle adjacent
                case MOVE:
                    return new MoveNodeExecutor().Execute(next, current, verbose);
                case STEP_UP:
                    return new StepUpNodeExecutor().Execute(next, current, verbose);
                case STEP_DOWN:
                    return new StepDownNodeExecutor().Execute(next, current, verbose);
            }
        } catch (Exception ex) { return false; }
        return false;
    }
}
