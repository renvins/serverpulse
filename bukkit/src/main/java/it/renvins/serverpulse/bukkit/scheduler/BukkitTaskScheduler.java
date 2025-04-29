package it.renvins.serverpulse.bukkit.scheduler;

import it.renvins.serverpulse.common.scheduler.Task;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;
import it.renvins.serverpulse.bukkit.ServerPulseBukkit;

public class BukkitTaskScheduler implements TaskScheduler {

    private final ServerPulseBukkit plugin;

    public BukkitTaskScheduler(ServerPulseBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runSync(Runnable task) {
        plugin.getServer().getScheduler().runTask(plugin, task);
    }

    @Override
    public void runAsync(Runnable task) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    @Override
    public Task runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        return new BukkitTaskWrapper(plugin.getServer().getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks));
    }

    @Override
    public Task runTaskTimerAsync(Runnable task, long delayTicks, long periodTicks) {
        return new BukkitTaskWrapper(plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks));
    }

    @Override
    public Task runTaskLater(Runnable task, long delayTicks) {
        return new BukkitTaskWrapper(plugin.getServer().getScheduler().runTaskLater(plugin, task, delayTicks));
    }

    @Override
    public Task runTaskLaterAsync(Runnable task, long delayTicks) {
        return new BukkitTaskWrapper(plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks));
    }
}
