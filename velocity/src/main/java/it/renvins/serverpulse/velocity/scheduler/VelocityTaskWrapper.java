package it.renvins.serverpulse.velocity.scheduler;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;
import it.renvins.serverpulse.common.scheduler.Task;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VelocityTaskWrapper implements Task {

    private final ScheduledTask task;

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return task.status() == TaskStatus.CANCELLED;
    }
}
