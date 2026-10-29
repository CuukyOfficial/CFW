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

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

/**
 * Scheduler for Folia. Compiled against the Paper API separately from the rest of CFW, so that nothing else depends on it: This class
 * is only loaded on Folia (see {@link FoliaPlatform}).
 */
class FoliaTaskScheduler implements TaskScheduler {

    private static final long MILLIS_PER_TICK = 50L;

    private final Plugin plugin;

    FoliaTaskScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final class Handle implements Task {

        private volatile ScheduledTask scheduled;
        private volatile boolean cancelled;

        void set(ScheduledTask scheduled) {
            if (scheduled == null) { // entity was already removed
                this.cancelled = true;
                return;
            }
            this.scheduled = scheduled;
            if (this.cancelled)
                scheduled.cancel();
        }

        @Override
        public void cancel() {
            this.cancelled = true;
            ScheduledTask s = this.scheduled;
            if (s != null)
                s.cancel();
        }

        @Override
        public boolean isCancelled() {
            if (this.cancelled)
                return true;
            ScheduledTask s = this.scheduled;
            return s != null && s.isCancelled();
        }
    }

    /** Folia does not accept delays/periods &lt; 1 */
    private static long ticks(long ticks) {
        return Math.max(1L, ticks);
    }

    private static Consumer<ScheduledTask> wrap(Handle handle, Consumer<Task> task) {
        return scheduled -> {
            handle.scheduled = scheduled;
            if (handle.cancelled) {
                scheduled.cancel();
                return;
            }
            task.accept(handle);
        };
    }

    @Override
    public boolean isRegionized() {
        return true;
    }

    @Override
    public Task run(Consumer<Task> task) {
        Handle handle = new Handle();
        handle.set(Bukkit.getGlobalRegionScheduler().run(this.plugin, wrap(handle, task)));
        return handle;
    }

    @Override
    public Task runLater(Consumer<Task> task, long delay) {
        Handle handle = new Handle();
        handle.set(Bukkit.getGlobalRegionScheduler().runDelayed(this.plugin, wrap(handle, task), ticks(delay)));
        return handle;
    }

    @Override
    public Task runTimer(Consumer<Task> task, long delay, long period) {
        Handle handle = new Handle();
        handle.set(Bukkit.getGlobalRegionScheduler().runAtFixedRate(this.plugin, wrap(handle, task), ticks(delay), ticks(period)));
        return handle;
    }

    @Override
    public Task runAsync(Consumer<Task> task) {
        Handle handle = new Handle();
        handle.set(Bukkit.getAsyncScheduler().runNow(this.plugin, wrap(handle, task)));
        return handle;
    }

    @Override
    public Task runAsyncLater(Consumer<Task> task, long delay) {
        Handle handle = new Handle();
        handle.set(Bukkit.getAsyncScheduler().runDelayed(this.plugin, wrap(handle, task), ticks(delay) * MILLIS_PER_TICK, TimeUnit.MILLISECONDS));
        return handle;
    }

    @Override
    public Task runAsyncTimer(Consumer<Task> task, long delay, long period) {
        Handle handle = new Handle();
        handle.set(Bukkit.getAsyncScheduler().runAtFixedRate(this.plugin, wrap(handle, task), ticks(delay) * MILLIS_PER_TICK,
                ticks(period) * MILLIS_PER_TICK, TimeUnit.MILLISECONDS));
        return handle;
    }

    @Override
    public Task runAt(Location location, Consumer<Task> task) {
        Handle handle = new Handle();
        handle.set(Bukkit.getRegionScheduler().run(this.plugin, location, wrap(handle, task)));
        return handle;
    }

    @Override
    public Task runAtLater(Location location, Consumer<Task> task, long delay) {
        Handle handle = new Handle();
        handle.set(Bukkit.getRegionScheduler().runDelayed(this.plugin, location, wrap(handle, task), ticks(delay)));
        return handle;
    }

    @Override
    public Task runAtTimer(Location location, Consumer<Task> task, long delay, long period) {
        Handle handle = new Handle();
        handle.set(Bukkit.getRegionScheduler().runAtFixedRate(this.plugin, location, wrap(handle, task), ticks(delay), ticks(period)));
        return handle;
    }

    @Override
    public Task runFor(Entity entity, Consumer<Task> task) {
        Handle handle = new Handle();
        handle.set(entity.getScheduler().run(this.plugin, wrap(handle, task), null));
        return handle;
    }

    @Override
    public Task runForLater(Entity entity, Consumer<Task> task, long delay) {
        Handle handle = new Handle();
        handle.set(entity.getScheduler().runDelayed(this.plugin, wrap(handle, task), null, ticks(delay)));
        return handle;
    }

    @Override
    public Task runForTimer(Entity entity, Consumer<Task> task, long delay, long period) {
        Handle handle = new Handle();
        handle.set(entity.getScheduler().runAtFixedRate(this.plugin, wrap(handle, task), null, ticks(delay), ticks(period)));
        return handle;
    }

    @Override
    public void execute(Runnable task) {
        if (Bukkit.isGlobalTickThread())
            task.run();
        else
            this.run(task);
    }

    @Override
    public void executeAt(Location location, Runnable task) {
        if (Bukkit.isOwnedByCurrentRegion(location))
            task.run();
        else
            this.runAt(location, task);
    }

    @Override
    public void executeFor(Entity entity, Runnable task) {
        if (Bukkit.isOwnedByCurrentRegion(entity))
            task.run();
        else
            this.runFor(entity, task);
    }

    @Override
    public void cancelTasks() {
        Bukkit.getGlobalRegionScheduler().cancelTasks(this.plugin);
        Bukkit.getAsyncScheduler().cancelTasks(this.plugin);
        Holder.remove(this.plugin);
    }
}
