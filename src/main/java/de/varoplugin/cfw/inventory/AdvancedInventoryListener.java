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

package de.varoplugin.cfw.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

class AdvancedInventoryListener implements Listener {

    private final AdvancedInventoryManager manager;

    AdvancedInventoryListener(AdvancedInventoryManager manager) {
        this.manager = manager;
    }

    private AdvancedInventory getInventory(Inventory inventory) {
        return this.manager.getInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        if (clicked == null || event.getCurrentItem() == null)
            return;

        AdvancedInventory inv = this.manager.getInventory(clicked);
        if (inv == null) {
            if (this.manager.getPrevInventory(clicked) != null)
                event.setCancelled(true);
            return;
        }
        inv.slotClicked(event.getSlot(), event);
        inv.runOptionalEventNotification(inv, e -> e.onInventoryClick(event));
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        AdvancedInventory inv = this.getInventory(event.getInventory());
        if (inv == null) {
            if (this.manager.getPrevInventory(event.getInventory()) != null)
                event.setCancelled(true);
            return;
        }
        inv.runOptionalEventNotification(inv, e -> e.onInventoryDrag(event));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        AdvancedInventory inv = this.getInventory(event.getInventory());
        if (inv == null)
            return;
        inv.inventoryClosed();
        inv.runOptionalEventNotification(inv, e -> e.onInventoryClose(event));
    }
}
