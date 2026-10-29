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
 * On regionized servers (Folia, see {@link #isRegionized()}) blocks and entities may only be accessed by the thread that owns them. Tasks are
 * therefore executed by the thread owning the given location/entity (or the global region). On all other servers every task that is not
 * asynchronous runs on the main thread and the location/entity arguments are ignored. Code written against this interface behaves identically
 * on both kinds of servers.
 * <p>
 * Delays and periods are measured in ticks. Values smaller than 1 are treated as 1 where the platform requires it.
 * <p>
 * The methods are grouped by the thread that executes the task:
 * <ul>
 * <li>{@code run*}: global region (main thread on non-Folia servers). Required for world settings like time, weather or game rules.</li>
 * <li>{@code runAsync*}: an asynchronous thread</li>
 * <li>{@code runAt*}: the region owning a {@link Location}. Required for blocks.</li>
 * <li>{@code runFor*}: the region owning an {@link Entity}. Required for players and other entities. The task is silently dropped if the entity
 * is removed.</li>
 * <li>{@code execute*}: like the above, but the task is run immediately if the current thread already is the right one</li>
 * </ul>
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
     * Teleports an entity without needing a plugin instance. On regionized servers this is performed asynchronously (loading the chunk if
     * necessary), otherwise synchronously.
     *
     * @param entity   The entity
     * @param location The target location
     * @return Future that completes with whether the teleport was successful
     */
    static CompletableFuture<Boolean> teleportAsync(Entity entity, Location location) {
        return Holder.PLATFORM.teleport(entity, location);
    }

    /**
     * @return Whether the server runs the world in multiple regions ticked by different threads (Folia)
     */
    boolean isRegionized();

    /**
     * Runs a task on the global region on the next tick.
     *
     * @param task The task. The argument is the handle of the task itself (e.g. to cancel it).
     * @return The task handle
     */
    Task run(Consumer<Task> task);

    /**
     * Runs a task on the global region after a delay.
     *
     * @param task  The task
     * @param delay Delay in ticks
     * @return The task handle
     */
    Task runLater(Consumer<Task> task, long delay);

    /**
     * Repeatedly runs a task on the global region.
     *
     * @param task   The task
     * @param delay  Delay in ticks before the first execution
     * @param period Ticks between two executions
     * @return The task handle
     */
    Task runTimer(Consumer<Task> task, long delay, long period);

    /**
     * @param task The task
     * @return The task handle
     * @see #run(Consumer)
     */
    default Task run(Runnable task) {
        return this.run(t -> task.run());
    }

    /**
     * @param task  The task
     * @param delay Delay in ticks
     * @return The task handle
     * @see #runLater(Consumer, long)
     */
    default Task runLater(Runnable task, long delay) {
        return this.runLater(t -> task.run(), delay);
    }

    /**
     * @param task   The task
     * @param delay  Delay in ticks before the first execution
     * @param period Ticks between two executions
     * @return The task handle
     * @see #runTimer(Consumer, long, long)
     */
    default Task runTimer(Runnable task, long delay, long period) {
        return this.runTimer(t -> task.run(), delay, period);
    }

    /**
     * Runs a task asynchronously.
     *
     * @param task The task
     * @return The task handle
     */
    Task runAsync(Consumer<Task> task);

    /**
     * Runs a task asynchronously after a delay.
     *
     * @param task  The task
     * @param delay Delay in ticks
     * @return The task handle
     */
    Task runAsyncLater(Consumer<Task> task, long delay);

    /**
     * Repeatedly runs a task asynchronously.
     *
     * @param task   The task
     * @param delay  Delay in ticks before the first execution
     * @param period Ticks between two executions
     * @return The task handle
     */
    Task runAsyncTimer(Consumer<Task> task, long delay, long period);

    /**
     * @param task The task
     * @return The task handle
     * @see #runAsync(Consumer)
     */
    default Task runAsync(Runnable task) {
        return this.runAsync(t -> task.run());
    }

    /**
     * @param task  The task
     * @param delay Delay in ticks
     * @return The task handle
     * @see #runAsyncLater(Consumer, long)
     */
    default Task runAsyncLater(Runnable task, long delay) {
        return this.runAsyncLater(t -> task.run(), delay);
    }

    /**
     * @param task   The task
     * @param delay  Delay in ticks before the first execution
     * @param period Ticks between two executions
     * @return The task handle
     * @see #runAsyncTimer(Consumer, long, long)
     */
    default Task runAsyncTimer(Runnable task, long delay, long period) {
        return this.runAsyncTimer(t -> task.run(), delay, period);
    }

    /**
     * Runs a task on the region owning the location on the next tick.
     *
     * @param location The location
     * @param task     The task
     * @return The task handle
     */
    Task runAt(Location location, Consumer<Task> task);

    /**
     * Runs a task on the region owning the location after a delay.
     *
     * @param location The location
     * @param task     The task
     * @param delay    Delay in ticks
     * @return The task handle
     */
    Task runAtLater(Location location, Consumer<Task> task, long delay);

    /**
     * Repeatedly runs a task on the region owning the location.
     *
     * @param location The location
     * @param task     The task
     * @param delay    Delay in ticks before the first execution
     * @param period   Ticks between two executions
     * @return The task handle
     */
    Task runAtTimer(Location location, Consumer<Task> task, long delay, long period);

    /**
     * @param location The location
     * @param task     The task
     * @return The task handle
     * @see #runAt(Location, Consumer)
     */
    default Task runAt(Location location, Runnable task) {
        return this.runAt(location, t -> task.run());
    }

    /**
     * @param location The location
     * @param task     The task
     * @param delay    Delay in ticks
     * @return The task handle
     * @see #runAtLater(Location, Consumer, long)
     */
    default Task runAtLater(Location location, Runnable task, long delay) {
        return this.runAtLater(location, t -> task.run(), delay);
    }

    /**
     * @param location The location
     * @param task     The task
     * @param delay    Delay in ticks before the first execution
     * @param period   Ticks between two executions
     * @return The task handle
     * @see #runAtTimer(Location, Consumer, long, long)
     */
    default Task runAtTimer(Location location, Runnable task, long delay, long period) {
        return this.runAtTimer(location, t -> task.run(), delay, period);
    }

    /**
     * Runs a task on the region owning the entity on the next tick. The task is silently dropped if the entity is removed.
     *
     * @param entity The entity
     * @param task   The task
     * @return The task handle
     */
    Task runFor(Entity entity, Consumer<Task> task);

    /**
     * Runs a task on the region owning the entity after a delay. The task is silently dropped if the entity is removed.
     *
     * @param entity The entity
     * @param task   The task
     * @param delay  Delay in ticks
     * @return The task handle
     */
    Task runForLater(Entity entity, Consumer<Task> task, long delay);

    /**
     * Repeatedly runs a task on the region owning the entity. The task is silently dropped if the entity is removed.
     *
     * @param entity The entity
     * @param task   The task
     * @param delay  Delay in ticks before the first execution
     * @param period Ticks between two executions
     * @return The task handle
     */
    Task runForTimer(Entity entity, Consumer<Task> task, long delay, long period);

    /**
     * @param entity The entity
     * @param task   The task
     * @return The task handle
     * @see #runFor(Entity, Consumer)
     */
    default Task runFor(Entity entity, Runnable task) {
        return this.runFor(entity, t -> task.run());
    }

    /**
     * @param entity The entity
     * @param task   The task
     * @param delay  Delay in ticks
     * @return The task handle
     * @see #runForLater(Entity, Consumer, long)
     */
    default Task runForLater(Entity entity, Runnable task, long delay) {
        return this.runForLater(entity, t -> task.run(), delay);
    }

    /**
     * @param entity The entity
     * @param task   The task
     * @param delay  Delay in ticks before the first execution
     * @param period Ticks between two executions
     * @return The task handle
     * @see #runForTimer(Entity, Consumer, long, long)
     */
    default Task runForTimer(Entity entity, Runnable task, long delay, long period) {
        return this.runForTimer(entity, t -> task.run(), delay, period);
    }

    /**
     * Runs the task immediately if called from the main thread (the global region thread on Folia), otherwise schedules it on the global region.
     * Callers must not rely on the task having been completed when this method returns.
     *
     * @param task The task
     */
    void execute(Runnable task);

    /**
     * Runs the task immediately if the current thread owns the region of the location (always the main thread on non-Folia servers), otherwise
     * schedules it on that region. Callers must not rely on the task having been completed when this method returns.
     *
     * @param location The location
     * @param task     The task
     */
    void executeAt(Location location, Runnable task);

    /**
     * Runs the task immediately if the current thread owns the entity (always the main thread on non-Folia servers), otherwise schedules it on
     * the entity's region. Callers must not rely on the task having been completed when this method returns.
     *
     * @param entity The entity
     * @param task   The task
     */
    void executeFor(Entity entity, Runnable task);

    /**
     * Teleports an entity.
     *
     * @param entity   The entity
     * @param location The target location
     * @return Future that completes with whether the teleport was successful
     * @see #teleportAsync(Entity, Location)
     */
    default CompletableFuture<Boolean> teleport(Entity entity, Location location) {
        return teleportAsync(entity, location);
    }

    /**
     * Cancels all global and asynchronous tasks of the plugin (on non-Folia servers all tasks) and releases this scheduler. Call this when the
     * plugin is disabled.
     */
    void cancelTasks();

    /**
     * Holds the scheduler instances. Not part of the API.
     */
    final class Holder {

        static final Platform PLATFORM = Platform.current();
        private static final Map<Plugin, TaskScheduler> SCHEDULERS = new WeakHashMap<>();

        private Holder() {
            // nop
        }

        static synchronized void remove(Plugin plugin) {
            SCHEDULERS.remove(plugin);
        }

        private static synchronized TaskScheduler of(Plugin plugin) {
            return SCHEDULERS.computeIfAbsent(plugin, PLATFORM::createScheduler);
        }
    }
}
