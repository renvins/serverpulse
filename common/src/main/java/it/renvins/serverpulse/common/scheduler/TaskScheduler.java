package it.renvins.serverpulse.common.scheduler;

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
     * Runs a task timer synchronously on the main thread.
     *
     * @param task The task to run
     * @throws UnsupportedOperationException if the implementation does not support synchronous task execution
     */
    default Task runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        throw new UnsupportedOperationException("Synchronous task execution is not supported.");
    }

    /**
     * Runs a task later synchronously on the main thread.
     *
     * @param task The task to run
     * @throws UnsupportedOperationException if the implementation does not support synchronous task execution
     */
    default Task runTaskLater(Runnable task, long delayTicks) {
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
     * Runs a task later asynchronously on a separate thread.
     *
     * @param task The task to run
     * @return A Task object representing the task
     */
    Task runTaskLaterAsync(Runnable task, long delayTicks);
}
