package de.cuuky.cfw.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

class AdvancedInventoryListener implements Listener {

    private final AdvancedInventoryManager manager;

    AdvancedInventoryListener(AdvancedInventoryManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        if (clicked == null || event.getCurrentItem() == null)
            return;

        AdvancedInventory inv = this.manager.getInventory(clicked);
        if (inv == null)
            return;

        inv.slotClicked(event.getSlot(), event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory clicked = event.getInventory();
        AdvancedInventory inv = this.manager.getInventory(clicked);
        if (inv == null)
            return;

        inv.inventoryClosed(event);
    }
}