package it.renvins.serverpulse.common.scheduler;

public interface Task {

    void cancel();
    boolean isCancelled();

}
