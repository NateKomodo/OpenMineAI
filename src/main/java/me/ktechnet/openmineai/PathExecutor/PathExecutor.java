package me.ktechnet.openmineai.PathExecutor;

import me.ktechnet.openmineai.Helpers.*;
import me.ktechnet.openmineai.Main;
import me.ktechnet.openmineai.Models.Classes.Pos;
import me.ktechnet.openmineai.Models.ConfigData.Settings;
import me.ktechnet.openmineai.Models.Enums.ExecutionResult;
import me.ktechnet.openmineai.Models.Enums.MoveDirection;
import me.ktechnet.openmineai.Models.Enums.NodeType;
import me.ktechnet.openmineai.Models.Interfaces.INode;
import me.ktechnet.openmineai.Models.Interfaces.IPathExecutionCallback;
import me.ktechnet.openmineai.Models.Interfaces.IPathExecutor;
import me.ktechnet.openmineai.Models.Interfaces.IRoute;
import me.ktechnet.openmineai.PathExecutor.NodeExecutors.MoveNodeExecutor;
import me.ktechnet.openmineai.PathExecutor.NodeExecutors.StepDownNodeExecutor;
import me.ktechnet.openmineai.PathExecutor.NodeExecutors.StepUpNodeExecutor;
import me.ktechnet.openmineai.PathExecutor.NodeExecutors.SwimNodeExecutor;
import me.ktechnet.openmineai.Pathfinder.Node;
import me.ktechnet.openmineai.Pathfinder.RuleEvaluator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class PathExecutor implements IPathExecutor {
    private final PlayerControl pc = new PlayerControl();
    private final EntityPlayerSP player = Minecraft.getMinecraft().player;

    private int i = 0;

    private IRoute route;

    private boolean abort = false;

    @Override
    public void ExecutePath(IRoute route, IPathExecutionCallback callback, boolean verbose) {
        pc.TakeControl();
        this.route = route;
        if (route.path().size() - 1 != 0) {
            Thread t = new Thread(() -> {
                if (verbose) ChatMessageHandler.SendMessage("Starting execution");
                boolean complete = false;
                for (i = 0; i < route.path().size() - 1; i++) {
                    if (abort) {
                        if (verbose) ChatMessageHandler.SendMessage("Abort");
                        return;
                    }
                    if (!ExecuteNode(route.path().get(i + 1), route.path().get(i), verbose)) {
                        if (verbose) ChatMessageHandler.SendMessage("Execute manager reports failed!");
                        break;
                    }
                }
                if (route.path().get(route.path().size() - 1).pos().IsEqual(new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ))) { //Check we are now at end
                    if (verbose) ChatMessageHandler.SendMessage("Path execution complete");
                    callback.pathExecutionSuccess();
                } else {
                    if (verbose) ChatMessageHandler.SendMessage("Reached end: Path execution failed");
                    callback.pathExecutionFailed();
                }
            });
            t.setDaemon(true);
            t.start();
        }
        else {
            if (verbose) ChatMessageHandler.SendMessage("Insufficient nodes");
            callback.pathExecutionSuccess();
        }
    }

    @Override
    public void Abort() {
        abort = true;
    }

    private boolean ExecuteNode(INode next, INode current, boolean verbose) { //TODO execute nodes (move into/etc) and see if we can shortcut/save
        Pos myPos = current.myType() == NodeType.SWIM ? new Pos((int)player.posX, current.pos().y, (int)player.posZ) : new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ);
        if (!current.pos().IsEqual(myPos)) { //Check we are indeed at current
            if (verbose) ChatMessageHandler.SendMessage("No longer on route, abort. Expected: " + current.pos().toString() + " Found: " + myPos.toString());
            ExecutionResult returnSuccess = ReturnToRoute(GetClosest(), verbose);
            return returnSuccess != ExecutionResult.FAILED && returnSuccess != ExecutionResult.OFF_PATH;
        }
        ExecutionResult result = ExecutionManager(next, current, verbose);
        if (result == ExecutionResult.FAILED) {
            if (verbose) ChatMessageHandler.SendMessage("Execution returned failed!");
            return false; //Execute
        } else if (result == ExecutionResult.OFF_PATH) {
            ExecutionResult returnSuccess = ReturnToRoute(GetClosest(), verbose);
            if (verbose) ChatMessageHandler.SendMessage("Execution offpath call finished renav: " + (returnSuccess != ExecutionResult.FAILED && returnSuccess != ExecutionResult.OFF_PATH));
            return returnSuccess != ExecutionResult.FAILED && returnSuccess != ExecutionResult.OFF_PATH;
        }
        Pos myNewPos = current.myType() == NodeType.SWIM ? new Pos((int)player.posX, next.pos().y, (int)player.posZ) : new Pos((int)player.posX, (int)Math.ceil(player.posY), (int)player.posZ);
        if (!next.pos().IsEqual(myNewPos)) { //Check we are now at end
            if (verbose) ChatMessageHandler.SendMessage("No longer on route, abort. Expected: " + next.pos() + " Found: " + myNewPos.toString());
            ExecutionResult returnSuccess = ReturnToRoute(GetClosest(), verbose);
            if (verbose) ChatMessageHandler.SendMessage("Execution offpath call finished renav: " + (returnSuccess != ExecutionResult.FAILED && returnSuccess != ExecutionResult.OFF_PATH));
            return returnSuccess != ExecutionResult.FAILED && returnSuccess != ExecutionResult.OFF_PATH;
        }
        return true; //Execution good and checks passed
    }
    private ExecutionResult ExecutionManager(INode next, INode current, boolean verbose) {
        try {
            if (verbose) ChatMessageHandler.SendMessage("Starting node execution: " + next.myType() + " at " + next.pos().toString());
            switch (next.myType()) { //NOTE destination node is only spawned if both move and break_and_move cannot reach it and therefore needs a special use case, or just idle adjacent
                case MOVE:
                    return new MoveNodeExecutor().Execute(next, current, verbose, false, ShouldTurn(current.pos(), next.pos()), Direction(DetermineProposedDirection(next.pos(), current.pos(), false), current.pos(), next.pos()));
                case STEP_UP:
                    return new StepUpNodeExecutor().Execute(next, current, verbose, false, ShouldTurn(current.pos(), next.pos()), Direction(DetermineProposedDirection(next.pos(), current.pos(), false), current.pos(), next.pos()));
                case STEP_DOWN:
                    return new StepDownNodeExecutor().Execute(next, current, verbose, false, ShouldTurn(current.pos(), next.pos()), Direction(DetermineProposedDirection(next.pos(), current.pos(), false), current.pos(), next.pos()));
                case SWIM:
                    return new SwimNodeExecutor().Execute(next, current, verbose, false, ShouldTurn(current.pos(), next.pos()), Direction(DetermineProposedDirection(next.pos(), current.pos(), false), current.pos(), next.pos()));
            }
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            printWriter.flush();
            String stackTrace = writer.toString();
            Main.logger.error(stackTrace);
            if (verbose) ChatMessageHandler.SendMessage("An error occurred during execution");
            return ExecutionResult.FAILED;
        }
        if (verbose) ChatMessageHandler.SendMessage("Fell out of switch case!");
        return ExecutionResult.FAILED;
    }
    private ExecutionResult ReturnToRoute(INode returnTo, boolean verbose) { //TODO swimming, fix stability issue or add retry
        try {
            GenerateRandomMovement(); //Generate some random movement to dislodge the player and centre them, in case they are on the edge of a block
            if (verbose) ChatMessageHandler.SendMessage("Returning to path: going to node at: " + returnTo.pos());
            Pos pos = new Pos((int) player.posX, (int) Math.ceil(player.posY), (int) player.posZ);
            Pos ret = returnTo.pos();
            if (DistanceHelper.CalcDistance(pos, ret) > 10) {
                if (verbose) ChatMessageHandler.SendMessage("Distance too great to return, abort");
                return ExecutionResult.FAILED;
            }
            int xOffset = ret.x - pos.x;
            int zOffset = ret.z - pos.z;
            final Node current = new Node(NodeType.PLAYER, null, null, 0, 0, pos, ret, 99, null, 99);
            final boolean diagonal = (Math.abs(xOffset) > 0) && (Math.abs(zOffset) > 0);
            if (!(Math.abs(xOffset) > 1) && !(Math.abs(zOffset) > 1)) {
                //Right next to node, no need to do much
                RuleEvaluator rev = new RuleEvaluator(new ArrayList<>(), new ArrayList<>(), pos, new Settings());
                NodeTypeRules r = new NodeTypeRules();
                Pos close = new Pos(ret.x, pos.y, ret.z); //Ensure y is the same in order to prevent move being triggered instead of step up
                if (rev.Evaluate(close, r.GetMove(diagonal))) {
                    if (verbose) ChatMessageHandler.SendMessage("RTP start exec of node move in close mode");
                    return new MoveNodeExecutor().Execute(new Node(NodeType.MOVE, null, null, 0, 0, close, ret, 99, null, 99), current, verbose, true, ShouldTurn(current.pos(), close), Direction(DetermineProposedDirection(close, current.pos(), true), current.pos(), close));
                } else if (rev.Evaluate(close, r.GetStepUp(diagonal))) {
                    if (verbose) ChatMessageHandler.SendMessage("RTP start exec of node step up in close mode");
                    pos.y++;
                    return new StepUpNodeExecutor().Execute(new Node(NodeType.STEP_UP, null, null, 0, 0, close, ret, 99, null, 99), current, verbose, true, ShouldTurn(current.pos(), close), Direction(DetermineProposedDirection(close, current.pos(), true), current.pos(), close));
                } else if (rev.Evaluate(close, r.GetStepDown(diagonal))) {
                    if (verbose) ChatMessageHandler.SendMessage("RTP start exec of node step down in close mode");
                    pos.y--;
                    return new StepDownNodeExecutor().Execute(new Node(NodeType.STEP_DOWN, null, null, 0, 0, close, ret, 99, null, 99), current, verbose, true, ShouldTurn(current.pos(), close), Direction(DetermineProposedDirection(close, current.pos(), true), current.pos(), close));
                } else {
                    if (verbose) ChatMessageHandler.SendMessage("Close ranged rule checks for return fell out");
                    return ExecutionResult.FAILED;
                }
            } else {
                int newXoffset = Integer.compare(xOffset, 0);
                int newZoffset = Integer.compare(zOffset, 0);
                Pos intermediate = new Pos(pos.x + newXoffset, pos.y, pos.z + newZoffset);
                RuleEvaluator rev = new RuleEvaluator(new ArrayList<>(), new ArrayList<>(), pos, new Settings());
                NodeTypeRules r = new NodeTypeRules();
                if (rev.Evaluate(intermediate, r.GetMove(diagonal))) {
                    if (verbose) ChatMessageHandler.SendMessage("RTP start exec of node move in intermediate mode");
                    new MoveNodeExecutor().Execute(new Node(NodeType.MOVE, null, null, 0, 0, intermediate, ret, 99, null, 99), current, verbose, true, ShouldTurn(current.pos(), intermediate), Direction(DetermineProposedDirection(intermediate, current.pos(), true), current.pos(), intermediate));
                } else if (rev.Evaluate(intermediate, r.GetStepUp(diagonal))) {
                    if (verbose) ChatMessageHandler.SendMessage("RTP start exec of node step up in intermediate mode");
                    pos.y++;
                    new StepUpNodeExecutor().Execute(new Node(NodeType.STEP_UP, null, null, 0, 0, intermediate, ret, 99, null, 99), current, verbose, true, ShouldTurn(current.pos(), intermediate), Direction(DetermineProposedDirection(intermediate, current.pos(), true), current.pos(), intermediate));
                } else if (rev.Evaluate(intermediate, r.GetStepDown(diagonal))) {
                    if (verbose) ChatMessageHandler.SendMessage("RTP start exec of node step down in intermediate mode");
                    pos.y--;
                    new StepDownNodeExecutor().Execute(new Node(NodeType.STEP_DOWN, null, null, 0, 0, intermediate, ret, 99, null, 99), current, verbose, true, ShouldTurn(current.pos(), intermediate), Direction(DetermineProposedDirection(intermediate, current.pos(), true), current.pos(), intermediate));
                } else {
                    if (verbose) ChatMessageHandler.SendMessage("Intermediate ranged rule checks for return fell out");
                    return ExecutionResult.FAILED;
                }
                return ReturnToRoute(returnTo, verbose);
            }
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            printWriter.flush();
            String stackTrace = writer.toString();
            Main.logger.error(stackTrace);
            if (verbose) ChatMessageHandler.SendMessage("An error occurred during execution");
            return ExecutionResult.FAILED;
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
                }
            } else {
                closest = node;
            }
        }
        if (closest != null) {
            //In order to better improve overall flow, we are going to bump loc up by 1 then get the result to try and move forward
            //loc++;
            closest = route.path().get(loc);
            this.i = (loc - 1);
        }
        return closest;
    }
    private String DetermineProposedDirection(Pos next, Pos current, boolean RTP) {
        int xOffset = Integer.compare(next.x - current.x, 0);
        int zOffset = Integer.compare(next.z - current.z, 0);
        String facing = new ExecutionHelper().GetCardinalFromFacing();
        String proposed = new ExecutionHelper().GetCardinal(xOffset, zOffset); //This is becoming null
        if (facing.equals(proposed)) {
            return proposed;
        } else {
            if (RTP) {
                return proposed;
            } else if (!facing.equals("")) {
                return facing;
            } else {
                return proposed;
            }
        }
    }
    private boolean ShouldTurn(Pos current, Pos next) {
        //Logic to determine if a turn should be taken
        if (route.path().size() > (i + 2)) {
            int xOffset = Integer.compare(next.x - current.x, 0);
            int zOffset = Integer.compare(next.z - current.z, 0);
            if (Math.abs(xOffset) > 0 && Math.abs(zOffset) > 0) return true; //Always turn if diagonal
            int score = 0;
            score += CheckArrayPos(i + 3, i + 2);
            score += CheckArrayPos(i + 4, i + 3); //If these are the same we are going to strafe instead
            return score >= 0;
        }
        return true;
    }
    private int CheckArrayPos(int pos, int cur) {
        if (route.path().size() > pos + 1) {
            INode next = route.path().get(pos);
            INode current = route.path().get(cur);
            int xOffset = Integer.compare(next.pos().x - current.pos().x, 0);
            int zOffset = Integer.compare(next.pos().z - current.pos().z, 0);
            if (Math.abs(xOffset) > 0 && Math.abs(zOffset) > 0) {
                return 1; //Should turn as diagonal
            }
            if (cur > 0) {
                if (new ExecutionHelper().GetCardinalFromFacing().length() == 2) {
                    return 1; //Should turn as diagonal
                }
                String cCardinal = new ExecutionHelper().GetCardinalFromFacing(); //The current facing
                String nCardinal = new ExecutionHelper().GetCardinal(xOffset, zOffset); //The one of the node in question
                if (cCardinal.length() == 2) cCardinal = cCardinal.substring(0, 1); //We only want significant direction and diagonal has already been ruled out
                if (nCardinal.length() == 2) nCardinal = nCardinal.substring(0, 1);
                if (cCardinal.equals(nCardinal)) return -1; //No need to turn as significant direction is the same
            } else {
                return 1; //Should turn as we are at start and current facing is unknown
            }
        } else {
            return 0; //Out of bounds of array so we have no say
        }
        return 0;
    }
    private MoveDirection Direction(String proposedDirection, Pos current, Pos next) {
            int XOffset = Integer.compare(next.x - current.x, 0);
            int ZOffset = Integer.compare(next.z - current.z, 0);
            String currentCardinal = new ExecutionHelper().GetCardinalFromFacing(); //We only need the axial part of the cardinal as diagonals are disallowed
            String nextCardinal = new ExecutionHelper().GetCardinal(XOffset, ZOffset);
            if (currentCardinal.length() == 2) currentCardinal = currentCardinal.substring(0, 1);
            if (nextCardinal.length() == 2) nextCardinal = nextCardinal.substring(0, 1);
            switch (currentCardinal) {
                case "N":
                    switch (nextCardinal) {
                        case "N":
                            return MoveDirection.FORWARD;
                        case "S":
                            return MoveDirection.BACK;
                        case "E":
                            return MoveDirection.RIGHT;
                        case "W":
                            return MoveDirection.LEFT;
                    }
                    break;
                case "S":
                    switch (nextCardinal) {
                        case "N":
                            return MoveDirection.BACK;
                        case "S":
                            return MoveDirection.FORWARD;
                        case "E":
                            return MoveDirection.LEFT;
                        case "W":
                            return MoveDirection.RIGHT;
                    }
                    break;
                case "E":
                    switch (nextCardinal) {
                        case "N":
                            return MoveDirection.LEFT;
                        case "S":
                            return MoveDirection.RIGHT;
                        case "E":
                            return MoveDirection.FORWARD;
                        case "W":
                            return MoveDirection.BACK;
                    }
                    break;
                case "W":
                    switch (nextCardinal) {
                        case "N":
                            return MoveDirection.RIGHT;
                        case "S":
                            return MoveDirection.LEFT;
                        case "E":
                            return MoveDirection.BACK;
                        case "W":
                            return MoveDirection.FORWARD;
                    }
                    break;
        }
        return null;
    }
    private void GenerateRandomMovement() throws InterruptedException {
        PlayerControl.MoveForward = true;
        PlayerControl.StrafeRight = true;
        Thread.sleep(5);
        PlayerControl.MoveForward = false;
        PlayerControl.StrafeRight = false;
        PlayerControl.MoveBack = true;
        PlayerControl.StrafeLeft = true;
        Thread.sleep(10);
        PlayerControl.MoveBack = false;
        PlayerControl.StrafeLeft = false;
        Thread.sleep(100);
    }
}
