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

package de.cuuky.cfw.inventory.inserter;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.Info;
import de.cuuky.cfw.inventory.ItemInserter;

public class AnimatedClosingInserter implements ItemInserter {

    private boolean cancelled, started;

    private boolean setItem(AdvancedInventory inventory, Map<Integer, ItemStack> items, Player player, int index, int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (cancelled)
            return false;
        this.setItem(inventory, index, items.get(index), false);
        if (!inventory.isUpdating())
            player.updateInventory();
        return true;
    }

    @Override
    public void setItems(JavaPlugin plugin, AdvancedInventory inventory, Map<Integer, ItemStack> items, Player player, int size) {
        started = true;
        int delay = 300 / (inventory.getInfo(Info.SIZE));
        int middle = (size - 1) / 2;

        new BukkitRunnable() {

            boolean end = false;

            @Override
            public void run() {
                for (int index = 0; index <= middle * 2 + 1; index++) {
                    int tempIndex = (int) Math.floor((float) index / 2);
                    if (!setItem(inventory, items, player, (end ? size - tempIndex - 1 : tempIndex), delay))
                        return;

                    end = !end;
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void stopInserting() {
        this.cancelled = true;
    }

    @Override
    public boolean hasStarted() {
        return this.started;
    }
}
