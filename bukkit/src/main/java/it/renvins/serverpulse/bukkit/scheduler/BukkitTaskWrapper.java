package it.renvins.serverpulse.bukkit.scheduler;

import it.renvins.serverpulse.common.scheduler.Task;
import org.bukkit.scheduler.BukkitTask;

public class BukkitTaskWrapper implements Task {

    private final BukkitTask task;

    public BukkitTaskWrapper(BukkitTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }
}
