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

package de.varoplugin.cfw.player;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.varoplugin.cfw.scheduler.TaskScheduler;
import de.varoplugin.cfw.version.VersionUtils;

public final class SafeTeleport {

    private SafeTeleport() {
        // nop
    }

    /**
     * Teleports the player one block above the given location. On Folia the teleport is performed asynchronously, so the player might not
     * have arrived yet when this method returns. Use {@link #teleport(Player, Location)} to be notified.
     *
     * @param player   The player
     * @param location The location
     */
    public static void tp(Player player, Location location) {
        teleport(player, location);
    }

    /**
     * Teleports the player one block above the given location.
     *
     * @param player   The player
     * @param location The location
     * @return Future that completes with whether the teleport was successful
     */
    public static CompletableFuture<Boolean> teleport(Player player, Location location) {
        Location target = location.clone().add(0, 1, 0);

        if (VersionUtils.getServerSoftware().isRegionized()) {
            // Folia does not allow blocking chunk loads or synchronous teleports; teleportAsync loads the chunk itself
            return TaskScheduler.teleportAsync(player, target);
        }

        while (!location.getChunk().isLoaded())
            location.getChunk().load();

        Location blockUnder = location.clone().add(0, -1, 0);
        VersionUtils.getVersionAdapter().sendBlockChange(player, blockUnder, blockUnder.getBlock().getType());
        return CompletableFuture.completedFuture(player.teleport(target));
    }
}
