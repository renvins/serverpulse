package it.renvins.serverpulse.fabric.task;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import it.renvins.serverpulse.common.scheduler.Task;
import it.renvins.serverpulse.fabric.ServerPulseFabric;

public class FabricTask implements Task {

    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    private final Runnable runnable;
    private final boolean isPeriodic;
    private final long period;

    private long ticksUntilRun;

    public FabricTask(Runnable runnable, boolean isPeriodic, long delay, long period) {
        this.runnable = runnable;
        this.isPeriodic = isPeriodic;
        this.period = period;
        this.ticksUntilRun = delay;
    }

    @Override
    public void cancel() {
        cancelled.set(true);
    }

    @Override
    public boolean isCancelled() {
        return cancelled.get();
    }

    public void tick() {
        if (cancelled.get()) {
            return;
        }

        if (--ticksUntilRun <= 0) {
            try {
                runnable.run();
            } catch (Exception e) {
                ServerPulseFabric.LOGGER.log(Level.SEVERE, "An error occurred while executing task!", e);
            }
            if (isPeriodic && !cancelled.get()) {
                ticksUntilRun = period;
            } else {
                cancel();
            }
        }
    }
}
