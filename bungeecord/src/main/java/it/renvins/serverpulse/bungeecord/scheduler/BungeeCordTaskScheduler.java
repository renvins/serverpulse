package it.renvins.serverpulse.bungeecord.scheduler;

import it.renvins.serverpulse.bungeecord.ServerPulseBungeeCord;
import it.renvins.serverpulse.common.scheduler.Task;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;

import java.util.concurrent.TimeUnit;

public class BungeeCordTaskScheduler implements TaskScheduler {

    private final ServerPulseBungeeCord plugin;

    public BungeeCordTaskScheduler(ServerPulseBungeeCord plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsync(Runnable task) {
        plugin.getProxy().getScheduler().runAsync(plugin, task);
    }

    @Override
    public Task runTaskTimerAsync(Runnable task, long delayTicks, long periodTicks) {
        long delayMs = delayTicks * 50L; // 20 ticks/second = 50ms per tick
        long periodMs = periodTicks * 50L;

        return new BungeeCordTaskWrapper(
                plugin.getProxy().getScheduler().schedule(plugin, task, delayMs, periodMs, TimeUnit.MILLISECONDS));
    }
}
