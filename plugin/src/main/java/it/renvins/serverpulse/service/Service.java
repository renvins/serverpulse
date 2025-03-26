package it.renvins.serverpulse.service;

public interface Service {
    void load();
    default void unload() {}
}
