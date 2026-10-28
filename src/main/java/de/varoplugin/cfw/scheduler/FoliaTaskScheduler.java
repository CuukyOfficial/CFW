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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

/**
 * Scheduler for Folia. The Folia API is accessed via reflection so that CFW can still be compiled against (and loaded on) Spigot.
 */
class FoliaTaskScheduler implements TaskScheduler {

    private static final String REGIONIZED_SERVER = "io.papermc.paper.threadedregions.RegionizedServer";
    private static final String SCHEDULER_PACKAGE = "io.papermc.paper.threadedregions.scheduler.";

    private static final boolean SUPPORTED;
    private static Method isGlobalTickThread, isOwnedByLocation, isOwnedByEntity;
    private static Method getGlobal, getRegion, getAsync, getEntityScheduler, teleportAsync;
    private static Method globalRun, globalRunDelayed, globalRunFixed, globalCancel;
    private static Method regionRun, regionRunDelayed, regionRunFixed;
    private static Method asyncRun, asyncRunDelayed, asyncRunFixed, asyncCancel;
    private static Method entityRun, entityRunDelayed, entityRunFixed;
    private static Method taskCancel, taskIsCancelled;

    static {
        boolean supported;
        try {
            Class.forName(REGIONIZED_SERVER);

            Class<?> global = Class.forName(SCHEDULER_PACKAGE + "GlobalRegionScheduler");
            Class<?> region = Class.forName(SCHEDULER_PACKAGE + "RegionScheduler");
            Class<?> async = Class.forName(SCHEDULER_PACKAGE + "AsyncScheduler");
            Class<?> entity = Class.forName(SCHEDULER_PACKAGE + "EntityScheduler");
            Class<?> scheduled = Class.forName(SCHEDULER_PACKAGE + "ScheduledTask");

            getGlobal = Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler");
            getRegion = Bukkit.getServer().getClass().getMethod("getRegionScheduler");
            getAsync = Bukkit.getServer().getClass().getMethod("getAsyncScheduler");
            isGlobalTickThread = Bukkit.class.getMethod("isGlobalTickThread");
            isOwnedByLocation = Bukkit.class.getMethod("isOwnedByCurrentRegion", Location.class);
            isOwnedByEntity = Bukkit.class.getMethod("isOwnedByCurrentRegion", Entity.class);
            getEntityScheduler = Entity.class.getMethod("getScheduler");
            teleportAsync = Entity.class.getMethod("teleportAsync", Location.class);

            globalRun = global.getMethod("run", Plugin.class, Consumer.class);
            globalRunDelayed = global.getMethod("runDelayed", Plugin.class, Consumer.class, long.class);
            globalRunFixed = global.getMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class);
            globalCancel = global.getMethod("cancelTasks", Plugin.class);

            regionRun = region.getMethod("run", Plugin.class, Location.class, Consumer.class);
            regionRunDelayed = region.getMethod("runDelayed", Plugin.class, Location.class, Consumer.class, long.class);
            regionRunFixed = region.getMethod("runAtFixedRate", Plugin.class, Location.class, Consumer.class, long.class, long.class);

            asyncRun = async.getMethod("runNow", Plugin.class, Consumer.class);
            asyncRunDelayed = async.getMethod("runDelayed", Plugin.class, Consumer.class, long.class, TimeUnit.class);
            asyncRunFixed = async.getMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class, TimeUnit.class);
            asyncCancel = async.getMethod("cancelTasks", Plugin.class);

            entityRun = entity.getMethod("run", Plugin.class, Consumer.class, Runnable.class);
            entityRunDelayed = entity.getMethod("runDelayed", Plugin.class, Consumer.class, Runnable.class, long.class);
            entityRunFixed = entity.getMethod("runAtFixedRate", Plugin.class, Consumer.class, Runnable.class, long.class, long.class);

            taskCancel = scheduled.getMethod("cancel");
            taskIsCancelled = scheduled.getMethod("isCancelled");
            supported = true;
        } catch (ReflectiveOperationException | LinkageError | NullPointerException e) {
            supported = false;
        }
        SUPPORTED = supported;
    }

    static boolean isSupported() {
        return SUPPORTED;
    }

    private static final long MILLIS_PER_TICK = 50L;

    private final Plugin plugin;

    FoliaTaskScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final class Handle implements Task {

        private volatile Object scheduled;
        private volatile boolean cancelled;

        void set(Object scheduled) {
            if (scheduled == null) { // entity was already removed
                this.cancelled = true;
                return;
            }
            this.scheduled = scheduled;
            if (this.cancelled)
                invoke(taskCancel, scheduled);
        }

        @Override
        public void cancel() {
            this.cancelled = true;
            Object s = this.scheduled;
            if (s != null)
                invoke(taskCancel, s);
        }

        @Override
        public boolean isCancelled() {
            if (this.cancelled)
                return true;
            Object s = this.scheduled;
            return s != null && (boolean) invoke(taskIsCancelled, s);
        }
    }

    private static Object invoke(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException)
                throw (RuntimeException) cause;
            if (cause instanceof Error)
                throw (Error) cause;
            throw new IllegalStateException(cause);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Folia does not accept delays/periods &lt; 1 */
    private static long ticks(long ticks) {
        return Math.max(1L, ticks);
    }

    private static Consumer<Object> wrap(Handle handle, Consumer<Task> task) {
        return scheduled -> {
            handle.scheduled = scheduled;
            if (handle.cancelled) {
                invoke(taskCancel, scheduled);
                return;
            }
            task.accept(handle);
        };
    }

    private Object global() {
        return invoke(getGlobal, Bukkit.getServer());
    }

    private Object region() {
        return invoke(getRegion, Bukkit.getServer());
    }

    private Object async() {
        return invoke(getAsync, Bukkit.getServer());
    }

    @Override
    public boolean isFolia() {
        return true;
    }

    @Override
    public Task run(Consumer<Task> task) {
        Handle handle = new Handle();
        handle.set(invoke(globalRun, this.global(), this.plugin, wrap(handle, task)));
        return handle;
    }

    @Override
    public Task runLater(Consumer<Task> task, long delay) {
        Handle handle = new Handle();
        handle.set(invoke(globalRunDelayed, this.global(), this.plugin, wrap(handle, task), ticks(delay)));
        return handle;
    }

    @Override
    public Task runTimer(Consumer<Task> task, long delay, long period) {
        Handle handle = new Handle();
        handle.set(invoke(globalRunFixed, this.global(), this.plugin, wrap(handle, task), ticks(delay), ticks(period)));
        return handle;
    }

    @Override
    public Task runAsync(Consumer<Task> task) {
        Handle handle = new Handle();
        handle.set(invoke(asyncRun, this.async(), this.plugin, wrap(handle, task)));
        return handle;
    }

    @Override
    public Task runAsyncLater(Consumer<Task> task, long delay) {
        Handle handle = new Handle();
        handle.set(invoke(asyncRunDelayed, this.async(), this.plugin, wrap(handle, task), ticks(delay) * MILLIS_PER_TICK, TimeUnit.MILLISECONDS));
        return handle;
    }

    @Override
    public Task runAsyncTimer(Consumer<Task> task, long delay, long period) {
        Handle handle = new Handle();
        handle.set(invoke(asyncRunFixed, this.async(), this.plugin, wrap(handle, task), ticks(delay) * MILLIS_PER_TICK, ticks(period) * MILLIS_PER_TICK,
                TimeUnit.MILLISECONDS));
        return handle;
    }

    @Override
    public Task runAt(Location location, Consumer<Task> task) {
        Handle handle = new Handle();
        handle.set(invoke(regionRun, this.region(), this.plugin, location, wrap(handle, task)));
        return handle;
    }

    @Override
    public Task runAtLater(Location location, Consumer<Task> task, long delay) {
        Handle handle = new Handle();
        handle.set(invoke(regionRunDelayed, this.region(), this.plugin, location, wrap(handle, task), ticks(delay)));
        return handle;
    }

    @Override
    public Task runAtTimer(Location location, Consumer<Task> task, long delay, long period) {
        Handle handle = new Handle();
        handle.set(invoke(regionRunFixed, this.region(), this.plugin, location, wrap(handle, task), ticks(delay), ticks(period)));
        return handle;
    }

    @Override
    public Task runFor(Entity entity, Consumer<Task> task) {
        Handle handle = new Handle();
        handle.set(invoke(entityRun, invoke(getEntityScheduler, entity), this.plugin, wrap(handle, task), null));
        return handle;
    }

    @Override
    public Task runForLater(Entity entity, Consumer<Task> task, long delay) {
        Handle handle = new Handle();
        handle.set(invoke(entityRunDelayed, invoke(getEntityScheduler, entity), this.plugin, wrap(handle, task), null, ticks(delay)));
        return handle;
    }

    @Override
    public Task runForTimer(Entity entity, Consumer<Task> task, long delay, long period) {
        Handle handle = new Handle();
        handle.set(invoke(entityRunFixed, invoke(getEntityScheduler, entity), this.plugin, wrap(handle, task), null, ticks(delay), ticks(period)));
        return handle;
    }

    @Override
    public void execute(Runnable task) {
        if ((boolean) invoke(isGlobalTickThread, null))
            task.run();
        else
            this.run(task);
    }

    @Override
    public void executeAt(Location location, Runnable task) {
        if ((boolean) invoke(isOwnedByLocation, null, location))
            task.run();
        else
            this.runAt(location, task);
    }

    @Override
    public void executeFor(Entity entity, Runnable task) {
        if ((boolean) invoke(isOwnedByEntity, null, entity))
            task.run();
        else
            this.runFor(entity, task);
    }

    @SuppressWarnings("unchecked")
    static CompletableFuture<Boolean> teleport0(Entity entity, Location location) {
        return (CompletableFuture<Boolean>) invoke(teleportAsync, entity, location);
    }

    @Override
    public CompletableFuture<Boolean> teleport(Entity entity, Location location) {
        return teleport0(entity, location);
    }

    @Override
    public void cancelTasks() {
        invoke(globalCancel, this.global(), this.plugin);
        invoke(asyncCancel, this.async(), this.plugin);
    }
}
