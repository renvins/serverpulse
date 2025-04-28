package it.renvins.serverpulse.common.scheduler;

public interface TaskScheduler {

    void runSync(Runnable task);
    void runAsync(Runnable task);

    Task runTaskTimer(Runnable task, long delayTicks, long periodTicks);
    Task runTaskTimerAsync(Runnable task, long delayTicks, long periodTicks);

    Task runTaskLater(Runnable task, long delayTicks);
    Task runTaskLaterAsync(Runnable task, long delayTicks);
}
