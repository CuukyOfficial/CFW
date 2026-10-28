/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.varoplugin.cfw.scheduler;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Scheduler for all non-Folia servers. Delegates to the {@link org.bukkit.scheduler.BukkitScheduler}.
 */
class BukkitTaskScheduler implements TaskScheduler {

    private final Plugin plugin;

    BukkitTaskScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final class Handle implements Task {

        private volatile BukkitTask task;
        private volatile boolean cancelled;

        void set(BukkitTask task) {
            this.task = task;
            // cancel() might have been called before the task was assigned (e.g. async tasks)
            if (this.cancelled)
                task.cancel();
        }

        @Override
        public void cancel() {
            this.cancelled = true;
            BukkitTask t = this.task;
            if (t != null)
                t.cancel();
        }

        @Override
        public boolean isCancelled() {
            return this.cancelled;
        }
    }

    @Override
    public boolean isFolia() {
        return false;
    }

    @Override
    public Task run(Consumer<Task> task) {
        Handle handle = new Handle();
        handle.set(Bukkit.getScheduler().runTask(this.plugin, () -> task.accept(handle)));
        return handle;
    }

    @Override
    public Task runLater(Consumer<Task> task, long delay) {
        Handle handle = new Handle();
        handle.set(Bukkit.getScheduler().runTaskLater(this.plugin, () -> task.accept(handle), delay));
        return handle;
    }

    @Override
    public Task runTimer(Consumer<Task> task, long delay, long period) {
        Handle handle = new Handle();
        handle.set(Bukkit.getScheduler().runTaskTimer(this.plugin, () -> task.accept(handle), delay, period));
        return handle;
    }

    @Override
    public Task runAsync(Consumer<Task> task) {
        Handle handle = new Handle();
        handle.set(Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> task.accept(handle)));
        return handle;
    }

    @Override
    public Task runAsyncLater(Consumer<Task> task, long delay) {
        Handle handle = new Handle();
        handle.set(Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> task.accept(handle), delay));
        return handle;
    }

    @Override
    public Task runAsyncTimer(Consumer<Task> task, long delay, long period) {
        Handle handle = new Handle();
        handle.set(Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> task.accept(handle), delay, period));
        return handle;
    }

    @Override
    public Task runAt(Location location, Consumer<Task> task) {
        return this.run(task);
    }

    @Override
    public Task runAtLater(Location location, Consumer<Task> task, long delay) {
        return this.runLater(task, delay);
    }

    @Override
    public Task runAtTimer(Location location, Consumer<Task> task, long delay, long period) {
        return this.runTimer(task, delay, period);
    }

    @Override
    public Task runFor(Entity entity, Consumer<Task> task) {
        return this.run(task);
    }

    @Override
    public Task runForLater(Entity entity, Consumer<Task> task, long delay) {
        return this.runLater(task, delay);
    }

    @Override
    public Task runForTimer(Entity entity, Consumer<Task> task, long delay, long period) {
        return this.runTimer(task, delay, period);
    }

    @Override
    public CompletableFuture<Boolean> teleport(Entity entity, Location location) {
        return CompletableFuture.completedFuture(entity.teleport(location));
    }

    @Override
    public void cancelTasks() {
        Bukkit.getScheduler().cancelTasks(this.plugin);
    }
}
