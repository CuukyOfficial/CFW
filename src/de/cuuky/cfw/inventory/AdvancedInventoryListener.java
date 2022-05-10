package de.cuuky.cfw.inventory;

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
        if (clicked == null || event.getCurrentItem() == null) return;

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
        if (inv == null) return;
        inv.inventoryClosed();
        inv.runOptionalEventNotification(inv, e -> e.onInventoryClose(event));
    }
}