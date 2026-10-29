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

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import de.varoplugin.cfw.version.VersionUtils;

/**
 * The parts of the scheduling that depend on the server software.
 */
interface Platform {

    TaskScheduler createScheduler(Plugin plugin);

    CompletableFuture<Boolean> teleport(Entity entity, Location location);

    /**
     * @return The platform of the current server
     */
    static Platform current() {
        if (!VersionUtils.getServerSoftware().isRegionized())
            return new BukkitPlatform();

        try {
            return (Platform) Class.forName("de.varoplugin.cfw.scheduler.FoliaPlatform").getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException | LinkageError e) {
            throw new IllegalStateException("Unable to access the Folia scheduler API, this Folia version is not supported", e);
        }
    }

    final class BukkitPlatform implements Platform {

        @Override
        public TaskScheduler createScheduler(Plugin plugin) {
            return new BukkitTaskScheduler(plugin);
        }

        @Override
        public CompletableFuture<Boolean> teleport(Entity entity, Location location) {
            return CompletableFuture.completedFuture(entity.teleport(location));
        }
    }
}
