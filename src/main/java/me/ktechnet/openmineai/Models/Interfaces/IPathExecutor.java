package me.ktechnet.openmineai.Models.Interfaces;

public interface IPathExecutor {
    void ExecutePath(IRoute route, IPathExecutionCallback callback, boolean verbose);

    void Abort();
}
