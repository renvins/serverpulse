package it.renvins.serverpulse.bungeecord.scheduler;

import it.renvins.serverpulse.bungeecord.ServerPulseBungeeCord;
import it.renvins.serverpulse.common.scheduler.Task;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class BungeeCordTaskScheduler implements TaskScheduler {

    private final ServerPulseBungeeCord plugin;

    @Override
    public void runAsync(Runnable task) {
        plugin.getProxy().getScheduler().runAsync(plugin, task);
    }

    @Override
    public Task runTaskTimerAsync(Runnable task, long delayMs, long periodMs) {
        return new BungeeCordTaskWrapper(
                plugin.getProxy().getScheduler().schedule(plugin, task, delayMs, periodMs, TimeUnit.MILLISECONDS));
    }
}
