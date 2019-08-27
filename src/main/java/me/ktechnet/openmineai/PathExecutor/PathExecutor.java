package me.ktechnet.openmineai.PathExecutor;

import me.ktechnet.openmineai.Helpers.ChatMessageHandler;
import me.ktechnet.openmineai.Helpers.DistanceHelper;
import me.ktechnet.openmineai.Helpers.NodeTypeRules;
import me.ktechnet.openmineai.Helpers.PlayerControl;
import me.ktechnet.openmineai.Main;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.Settings;
import me.ktechnet.openmineai.Models.Enums.ExecutionResult;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IPathExecutionCallback;
import me.ktechnet.openmineai.Models.Interfaces.IPathExecutor;
import me.ktechnet.openmineai.Models.Interfaces.IRoute;
import me.ktechnet.openmineai.PathExecutor.NodeExecutors.MoveNodeExecutor;
import me.ktechnet.openmineai.PathExecutor.NodeExecutors.StepDownNodeExecutor;
import me.ktechnet.openmineai.PathExecutor.NodeExecutors.StepUpNodeExecutor;
import me.ktechnet.openmineai.Pathfinder.Node;
import me.ktechnet.openmineai.Pathfinder.RuleEvaluator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class PathExecutor implements IPathExecutor {
    private PlayerControl pc = new PlayerControl();
    private EntityPlayerSP player = Minecraft.getMinecraft().player;

    private int i = 0;

    private IRoute route;

    @Override
    public void ExecutePath(IRoute route, IPathExecutionCallback callback, boolean verbose) {
        pc.TakeControl();
        this.route = route;
        if (route.path().size() - 1 != 0) {
            new Thread(() -> {
                if (verbose) ChatMessageHandler.SendMessage("Starting execution");
                boolean complete = false;
                for (i = 0; i < route.path().size() - 1; i++) {
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
    //TODO abort system
    private boolean ExecuteNode(INode next, INode current, boolean verbose) { //TODO execute nodes (move into/etc) and see if we can shortcut/save, also see if we can return to trail if we get off it
        if (!current.pos().IsEqual(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ))) { //Check we are indeed at current
            if (verbose) ChatMessageHandler.SendMessage("No longer on route, abort");
            ExecutionResult returnSuccess = ReturnToRoute(GetClosest(), verbose);
            return returnSuccess != ExecutionResult.FAILED && returnSuccess != ExecutionResult.OFF_PATH;
        }
        ExecutionResult result = ExecutionManager(next, current, verbose);
        if (result == ExecutionResult.FAILED) {
            return false; //Execute, if failed return false
        } else if (result == ExecutionResult.OFF_PATH) {
            ExecutionResult returnSuccess = ReturnToRoute(GetClosest(), verbose);
            return returnSuccess != ExecutionResult.FAILED && returnSuccess != ExecutionResult.OFF_PATH;
        }
        if (!next.pos().IsEqual(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ))) { //Check we are now at end
            if (verbose) ChatMessageHandler.SendMessage("No longer on route, abort");
            ExecutionResult returnSuccess = ReturnToRoute(GetClosest(), verbose);
            return returnSuccess != ExecutionResult.FAILED && returnSuccess != ExecutionResult.OFF_PATH;
        }
        return true; //Execution good and checks passed
    }
    private ExecutionResult ExecutionManager(INode next, INode current, boolean verbose) {
        try {
            switch (next.myType()) { //NOTE destination node is only spawned if both move and break_and_move cannot reach it and therefore needs a special use case, or just idle adjacent
                case MOVE:
                    return new MoveNodeExecutor().Execute(next, current, verbose);
                case STEP_UP:
                    return new StepUpNodeExecutor().Execute(next, current, verbose);
                case STEP_DOWN:
                    return new StepDownNodeExecutor().Execute(next, current, verbose);
            }
        } catch (Exception ex) { return ExecutionResult.FAILED; }
        return ExecutionResult.FAILED;
    }
    private ExecutionResult ReturnToRoute(INode returnTo, boolean verbose) { //TODO make this return to closest point on route instead of last
        if (verbose) ChatMessageHandler.SendMessage("Returning to path: going to node at: " + returnTo.pos());
        Pos pos = new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ);
        Pos ret = returnTo.pos();
        int xOffset = ret.x - pos.x;
        int zOffset = ret.z - pos.z;
        final Node current = new Node(NodeType.PLAYER, null, null, 0, 0, pos, ret, 99, null, 99);
        final boolean diagonal = (Math.abs(xOffset) > 0) && (Math.abs(zOffset) > 0);
        if (!(Math.abs(xOffset) > 1) && !(Math.abs(zOffset) > 1)) {
            //Right next to node, no need to do much
            RuleEvaluator rev = new RuleEvaluator(new ArrayList<>(), new ArrayList<>(), pos, new Settings());
            NodeTypeRules r = new NodeTypeRules();
            if (rev.Evaluate(ret, r.GetMove(diagonal))) {
                return new MoveNodeExecutor().Execute(new Node(NodeType.MOVE, null, null, 0, 0, ret, ret, 99, null, 99), current, verbose);
            } else if (rev.Evaluate(ret, r.GetStepUp(diagonal))) {
                pos.y++;
                return new StepUpNodeExecutor().Execute(new Node(NodeType.STEP_UP, null, null, 0, 0, ret, ret, 99, null, 99), current, verbose);
            } else if (rev.Evaluate(ret, r.GetStepDown(diagonal))) {
                pos.y--;
                return new StepDownNodeExecutor().Execute(new Node(NodeType.STEP_DOWN, null, null, 0, 0, ret, ret, 99, null, 99), current, verbose);
            } else {
                return ExecutionResult.FAILED;
            }
        } else {
            int newXoffset = Integer.compare(xOffset, 0); //TODO make this get the right direction instead of a general direction
            int newZoffset = Integer.compare(zOffset, 0);
            Pos intermidiate = new Pos(pos.x + newXoffset, pos.y, pos.z + newZoffset);
            RuleEvaluator rev = new RuleEvaluator(new ArrayList<>(), new ArrayList<>(), pos, new Settings());
            NodeTypeRules r = new NodeTypeRules();
            if (rev.Evaluate(intermidiate, r.GetMove(diagonal))) {
                new MoveNodeExecutor().Execute(new Node(NodeType.MOVE, null, null, 0, 0, intermidiate, ret, 99, null, 99), current, verbose);
            } else if (rev.Evaluate(intermidiate, r.GetStepUp(diagonal))) {
                pos.y++;
                new StepUpNodeExecutor().Execute(new Node(NodeType.STEP_UP, null, null, 0, 0, intermidiate, ret, 99, null, 99), current, verbose);
            } else if (rev.Evaluate(intermidiate, r.GetStepDown(diagonal))) {
                pos.y--;
                new StepDownNodeExecutor().Execute(new Node(NodeType.STEP_DOWN, null, null, 0, 0, intermidiate, ret, 99, null, 99), current, verbose);
            } else {
                return ExecutionResult.FAILED;
            }
            return ReturnToRoute(returnTo, verbose);
        }
    }
    private INode GetClosest() {
        INode closest = null;
        int loc = 0;
        for (int j = 0; j < route.path().size(); j++) {
            INode node = route.path().get(j);
            if (closest != null) {
                double currentClosest = DistanceHelper.CalcDistance(closest.pos(), new Pos((int) player.posX, (int) Math.ceil(player.posY), (int) player.posZ));
                double myClosest = DistanceHelper.CalcDistance(node.pos(), new Pos((int) player.posX, (int) Math.ceil(player.posY), (int) player.posZ));
                if (myClosest < currentClosest) {
                    closest = node;
                    loc = j;
                };
            } else {
                closest = node;
            }
        }
        if (closest != null) {
            this.i = (loc - 1);
        }
        return closest;
    }
}
