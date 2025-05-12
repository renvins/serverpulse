package it.renvins.serverpulse.fabric.task;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ForkJoinPool;

import it.renvins.serverpulse.common.scheduler.Task;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class FabricScheduler implements TaskScheduler {

    private final Queue<FabricTask> tasks = new ConcurrentLinkedDeque<>();

    public FabricScheduler() {
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }

    @Override
    public void runSync(Runnable task) {
        FabricTask taskWrapper = new FabricTask(task, false, 0, 0);
        tasks.add(taskWrapper);
    }

    @Override
    public void runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        FabricTask fabricTask = new FabricTask(task, false, delayTicks, periodTicks);
        tasks.add(fabricTask);
    }

    @Override
    public void runAsync(Runnable task) {
        CompletableFuture.runAsync(task);
    }

    @Override
    public Task runTaskTimerAsync(Runnable task, long delayTicks, long periodTicks) {
        FabricTask fabricTask = new FabricTask(() -> CompletableFuture.runAsync(task),
                true, delayTicks, periodTicks);
        tasks.add(fabricTask);
        return fabricTask;
    }

    private void onServerTick(MinecraftServer server) {
        tasks.removeIf(task -> {
            if (task.isCancelled()) {
                return true;
            }
            task.tick();
            return task.isCancelled();
        });
    }
}
