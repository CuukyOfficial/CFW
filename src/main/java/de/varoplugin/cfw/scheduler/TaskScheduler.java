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

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

/**
 * Scheduler abstraction that works on Bukkit, Spigot, Paper and Folia.
 * <p>
 * On Folia, tasks are executed on the thread that owns the given region/entity. On all other servers everything that is not
 * asynchronous runs on the main thread and the region/entity arguments are ignored.
 * <p>
 * Delays and periods are measured in ticks. Values smaller than 1 are treated as 1 where the platform requires it.
 */
public interface TaskScheduler {

    /**
     * @param plugin Plugin that owns the tasks
     * @return The scheduler for this plugin (one instance per plugin)
     */
    static TaskScheduler of(Plugin plugin) {
        return Holder.of(plugin);
    }

    /**
     * Teleports an entity without needing a plugin instance. On Folia this is performed asynchronously (loading the chunk if
     * necessary), otherwise synchronously.
     *
     * @return Future that completes with whether the teleport was successful
     */
    static CompletableFuture<Boolean> teleportAsync(Entity entity, Location location) {
        return FoliaTaskScheduler.isSupported() ? FoliaTaskScheduler.teleport0(entity, location) : CompletableFuture.completedFuture(entity.teleport(location));
    }

    /**
     * @return Whether tasks are executed by Folia's regionized scheduler
     */
    boolean isFolia();

    // Global (main thread / global region)

    Task run(Consumer<Task> task);

    Task runLater(Consumer<Task> task, long delay);

    Task runTimer(Consumer<Task> task, long delay, long period);

    default Task run(Runnable task) {
        return this.run(t -> task.run());
    }

    default Task runLater(Runnable task, long delay) {
        return this.runLater(t -> task.run(), delay);
    }

    default Task runTimer(Runnable task, long delay, long period) {
        return this.runTimer(t -> task.run(), delay, period);
    }

    // Async

    Task runAsync(Consumer<Task> task);

    Task runAsyncLater(Consumer<Task> task, long delay);

    Task runAsyncTimer(Consumer<Task> task, long delay, long period);

    default Task runAsync(Runnable task) {
        return this.runAsync(t -> task.run());
    }

    default Task runAsyncLater(Runnable task, long delay) {
        return this.runAsyncLater(t -> task.run(), delay);
    }

    default Task runAsyncTimer(Runnable task, long delay, long period) {
        return this.runAsyncTimer(t -> task.run(), delay, period);
    }

    // Region (owner of a location)

    Task runAt(Location location, Consumer<Task> task);

    Task runAtLater(Location location, Consumer<Task> task, long delay);

    Task runAtTimer(Location location, Consumer<Task> task, long delay, long period);

    default Task runAt(Location location, Runnable task) {
        return this.runAt(location, t -> task.run());
    }

    default Task runAtLater(Location location, Runnable task, long delay) {
        return this.runAtLater(location, t -> task.run(), delay);
    }

    default Task runAtTimer(Location location, Runnable task, long delay, long period) {
        return this.runAtTimer(location, t -> task.run(), delay, period);
    }

    // Entity (owner of an entity). The task is silently dropped when the entity is removed.

    Task runFor(Entity entity, Consumer<Task> task);

    Task runForLater(Entity entity, Consumer<Task> task, long delay);

    Task runForTimer(Entity entity, Consumer<Task> task, long delay, long period);

    default Task runFor(Entity entity, Runnable task) {
        return this.runFor(entity, t -> task.run());
    }

    default Task runForLater(Entity entity, Runnable task, long delay) {
        return this.runForLater(entity, t -> task.run(), delay);
    }

    default Task runForTimer(Entity entity, Runnable task, long delay, long period) {
        return this.runForTimer(entity, t -> task.run(), delay, period);
    }

    // Immediate execution: runs the task right away if the current thread already owns the global region/location/entity, otherwise schedules it.

    /**
     * Runs the task immediately if called from the main thread (the global region thread on Folia), otherwise schedules it on the global region.
     */
    void execute(Runnable task);

    /**
     * Runs the task immediately if the current thread owns the region of the location (always the main thread on non-Folia servers), otherwise
     * schedules it on that region.
     */
    void executeAt(Location location, Runnable task);

    /**
     * Runs the task immediately if the current thread owns the entity (always the main thread on non-Folia servers), otherwise schedules it
     * on the entity's region.
     */
    void executeFor(Entity entity, Runnable task);

    /**
     * Teleports an entity. On Folia this is performed asynchronously (loading the chunk if necessary).
     *
     * @return Future that completes with whether the teleport was successful
     */
    CompletableFuture<Boolean> teleport(Entity entity, Location location);

    /**
     * Cancels all global and async tasks of the plugin (and on Bukkit all tasks).
     */
    void cancelTasks();

    final class Holder {

        private static final Map<Plugin, TaskScheduler> SCHEDULERS = new WeakHashMap<>();

        private Holder() {
            // nop
        }

        private static synchronized TaskScheduler of(Plugin plugin) {
            return SCHEDULERS.computeIfAbsent(plugin, p -> FoliaTaskScheduler.isSupported() ? new FoliaTaskScheduler(p) : new BukkitTaskScheduler(p));
        }
    }
}
