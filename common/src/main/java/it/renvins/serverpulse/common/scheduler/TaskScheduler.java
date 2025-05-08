package it.renvins.serverpulse.common.scheduler;

import java.util.concurrent.Executor;

public interface TaskScheduler {

    /**
     * Runs a task synchronously on the main thread.
     *
     * @param task The task to run
     * @throws UnsupportedOperationException if the implementation does not support synchronous task execution
     */
    default void runSync(Runnable task) {
        throw new UnsupportedOperationException("Synchronous task execution is not supported.");
    }

    /**
     * Runs a task asynchronously on a separate thread.
     *
     * @param task The task to run
     */
    void runAsync(Runnable task);

    /**
     * Runs a task timer asynchronously on a separate thread.
     *
     * @param task The task to run
     * @return A Task object representing the task
     */
    Task runTaskTimerAsync(Runnable task, long delayTicks, long periodTicks);


    /**
     * Returns an Executor that runs tasks synchronously on the main thread
     * or falls back to asynchronous execution if sync is not supported.
     *
     * @return An Executor for synchronous task execution
     */
    default Executor getSyncExecutor() {
        return task -> {
            try {
                runSync(task);
            } catch (UnsupportedOperationException e) {
                // Fallback to async execution if sync is not supported
                runAsync(task);
            }
        };
    }
}
