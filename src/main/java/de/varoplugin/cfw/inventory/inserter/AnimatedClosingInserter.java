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

package de.varoplugin.cfw.inventory.inserter;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import de.varoplugin.cfw.inventory.AdvancedInventory;
import de.varoplugin.cfw.inventory.ItemInserter;

public class AnimatedClosingInserter implements ItemInserter {

    private static final int DELAY = 1;
    private static final int AMOUNT = 4;

    private boolean started;
    private BukkitTask scheduler;
    private boolean end;
    private int index;

    protected void setItem(AdvancedInventory inventory, Map<Integer, ItemStack> items, Player player, int index) {
        this.setItem(inventory, index, items.get(index), false);
        if (!inventory.isUpdating())
            player.updateInventory();
    }

    protected void insertNext(AdvancedInventory inventory, Map<Integer, ItemStack> items, Player player, int size, int middle) {
        if (this.index > middle * 2 + 1) {
            if (this.scheduler != null)
                this.stopInserting();
            return;
        }

        int tempIndex = (int) Math.floor(this.index / 2F);
        this.setItem(inventory, items, player, (this.end ? size - tempIndex - 1 : tempIndex));

        this.end = !this.end;

        this.index++;
    }

    @Override
    public void setItems(JavaPlugin plugin, AdvancedInventory inventory, Map<Integer, ItemStack> items, Player player, int size) {
        this.started = true;
        int middle = (size - 1) / 2;

        this.scheduler = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (int i = 0; i < AMOUNT; i++)
                this.insertNext(inventory, items, player, size, middle);
        }, 0, DELAY);
    }

    @Override
    public void stopInserting() {
        if (this.scheduler != null) {
            this.scheduler.cancel();
            this.scheduler = null;
        }
    }

    @Override
    public boolean hasStarted() {
        return this.started;
    }
}
