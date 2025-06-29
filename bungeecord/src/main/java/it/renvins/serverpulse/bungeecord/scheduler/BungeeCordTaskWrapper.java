package it.renvins.serverpulse.bungeecord.scheduler;

import it.renvins.serverpulse.common.scheduler.Task;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.scheduler.ScheduledTask;

@RequiredArgsConstructor
public class BungeeCordTaskWrapper implements Task {

    private final ScheduledTask task;

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return task.getTask() == null;
    }
}
