package it.renvins.serverpulse.common.scheduler;

public interface Task {

    /**
     * Cancels the task
     */
    void cancel();

    /**
     * @return true if the task is cancelled
     */
    boolean isCancelled();

}
