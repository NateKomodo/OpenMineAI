package me.ktechnet.openmineai.Models.Interfaces;

import me.ktechnet.openmineai.Models.Enums.ExecutionResult;

public interface INodeTypeExecutor {
    ExecutionResult Execute(INode next, INode current, boolean verbose, boolean RTP) throws InterruptedException;
}
