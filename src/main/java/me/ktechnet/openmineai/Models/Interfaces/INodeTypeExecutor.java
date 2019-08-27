package me.ktechnet.openmineai.Models.Interfaces;

public interface INodeTypeExecutor {
    boolean Execute(INode next, INode current, boolean verbose) throws InterruptedException;
}
