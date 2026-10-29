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

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

/**
 * Drop-in replacement for {@link org.bukkit.scheduler.BukkitRunnable} that works on Folia as well.
 * <p>
 * The {@code runTask*} methods schedule on the global region (main thread on non-Folia servers), the {@code runTaskAt*} methods on the
 * region owning a location and the {@code runTaskFor*} methods on the entity's region.
 */
public abstract class TaskRunnable implements Runnable {

    private volatile Task task;
    private volatile boolean cancelled;

    private Task assign(Task task) {
        this.task = task;
        if (this.cancelled)
            task.cancel();
        return task;
    }

    private void execute(Task task) {
        this.task = task;
        this.run();
    }

    /**
     * Cancels this runnable. Can be called from within {@link #run()}.
     */
    public synchronized void cancel() {
        this.cancelled = true;
        Task t = this.task;
        if (t != null)
            t.cancel();
    }

    public boolean isCancelled() {
        Task t = this.task;
        return this.cancelled || (t != null && t.isCancelled());
    }

    public Task runTask(Plugin plugin) {
        return this.assign(TaskScheduler.of(plugin).run(this::execute));
    }

    public Task runTaskLater(Plugin plugin, long delay) {
        return this.assign(TaskScheduler.of(plugin).runLater(this::execute, delay));
    }

    public Task runTaskTimer(Plugin plugin, long delay, long period) {
        return this.assign(TaskScheduler.of(plugin).runTimer(this::execute, delay, period));
    }

    public Task runTaskAsynchronously(Plugin plugin) {
        return this.assign(TaskScheduler.of(plugin).runAsync(this::execute));
    }

    public Task runTaskLaterAsynchronously(Plugin plugin, long delay) {
        return this.assign(TaskScheduler.of(plugin).runAsyncLater(this::execute, delay));
    }

    public Task runTaskTimerAsynchronously(Plugin plugin, long delay, long period) {
        return this.assign(TaskScheduler.of(plugin).runAsyncTimer(this::execute, delay, period));
    }

    public Task runTaskAt(Plugin plugin, Location location) {
        return this.assign(TaskScheduler.of(plugin).runAt(location, this::execute));
    }

    public Task runTaskAtLater(Plugin plugin, Location location, long delay) {
        return this.assign(TaskScheduler.of(plugin).runAtLater(location, this::execute, delay));
    }

    public Task runTaskAtTimer(Plugin plugin, Location location, long delay, long period) {
        return this.assign(TaskScheduler.of(plugin).runAtTimer(location, this::execute, delay, period));
    }

    public Task runTaskFor(Plugin plugin, Entity entity) {
        return this.assign(TaskScheduler.of(plugin).runFor(entity, this::execute));
    }

    public Task runTaskForLater(Plugin plugin, Entity entity, long delay) {
        return this.assign(TaskScheduler.of(plugin).runForLater(entity, this::execute, delay));
    }

    public Task runTaskForTimer(Plugin plugin, Entity entity, long delay, long period) {
        return this.assign(TaskScheduler.of(plugin).runForTimer(entity, this::execute, delay, period));
    }
}
