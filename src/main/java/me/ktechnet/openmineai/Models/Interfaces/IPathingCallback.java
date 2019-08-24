package me.ktechnet.openmineai.Models.Interfaces;

public interface IPathingCallback {
    void completeRouteFound(IRoute route);

    void partialRouteFound(IRoute route);

    void alternateRouteFound(IRoute route);

    void outOfChunk(IRoute route);

    //TODO failed to find path
}
