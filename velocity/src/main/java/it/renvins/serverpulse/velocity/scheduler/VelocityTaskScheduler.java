package it.renvins.serverpulse.velocity.scheduler;

import java.util.concurrent.TimeUnit;

import it.renvins.serverpulse.common.scheduler.Task;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;
import it.renvins.serverpulse.velocity.ServerPulseVelocity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VelocityTaskScheduler implements TaskScheduler {

    private final ServerPulseVelocity plugin;

    @Override
    public void runAsync(Runnable task) {
        plugin.getServer().getScheduler().buildTask(plugin, task).schedule();
    }

    @Override
    public Task runTaskTimerAsync(Runnable task, long delayMs, long periodMs) {
        return new VelocityTaskWrapper(plugin.getServer().getScheduler().buildTask(plugin, task)
                        .delay(delayMs, TimeUnit.MILLISECONDS)
                        .repeat(periodMs, TimeUnit.MILLISECONDS)
                        .schedule());
    }
}
