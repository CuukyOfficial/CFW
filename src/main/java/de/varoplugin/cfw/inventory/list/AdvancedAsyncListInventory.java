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

package de.varoplugin.cfw.inventory.list;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.cryptomorin.xseries.XMaterial;

import de.varoplugin.cfw.inventory.AdvancedInventoryManager;
import de.varoplugin.cfw.inventory.ItemClick;
import de.varoplugin.cfw.item.EmptyItemBuilder;

public abstract class AdvancedAsyncListInventory<T> extends AdvancedListInventory<T> {

    public AdvancedAsyncListInventory(AdvancedInventoryManager manager, Player player, List<T> list) {
        super(manager, player, list);
    }

    protected ItemStack getLoadingItem() {
        return new EmptyItemBuilder().displayName("§cLoading...").material(XMaterial.COAL).build();
    }

    @Override
    protected void addShowItem(int index, ItemStack item, ItemClick click) {
        if (item == null)
            return;
        super.addItem(index, this.getLoadingItem());
        new BukkitRunnable() {
            @Override
            public void run() {
                AdvancedAsyncListInventory.super.addShowItem(index, item, click);
                AdvancedAsyncListInventory.this.getPlayer().updateInventory();
            }
        }.runTaskLaterAsynchronously(this.getManager().getPlugin(), 0L);
    }
}
