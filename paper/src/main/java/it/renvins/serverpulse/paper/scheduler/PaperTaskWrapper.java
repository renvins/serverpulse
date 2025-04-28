package it.renvins.serverpulse.paper.scheduler;

import it.renvins.serverpulse.common.scheduler.Task;
import org.bukkit.scheduler.BukkitTask;

public class PaperTaskWrapper implements Task {

    private final BukkitTask task;

    public PaperTaskWrapper(BukkitTask task) {
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
