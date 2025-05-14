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
    public Task runTaskTimerAsync(Runnable task, long delayTicks, long periodTicks) {
        return new VelocityTaskWrapper(plugin.getServer().getScheduler().buildTask(plugin, task)
                        .delay(delayTicks / 20, TimeUnit.SECONDS)
                        .repeat(periodTicks / 20, TimeUnit.SECONDS)
                        .schedule());
    }
}
