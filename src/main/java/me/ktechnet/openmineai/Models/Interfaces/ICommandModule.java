package me.ktechnet.openmineai.Models.Interfaces;

@SuppressWarnings("SameReturnValue")
public interface ICommandModule {
    void Run(String args[]);

    String GetArgs();
}
