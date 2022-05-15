package de.cuuky.cfw.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public interface EventNotifiable {

    void onInventoryClick(InventoryClickEvent event);

    void onInventoryClose(InventoryCloseEvent event);

    void onInventoryDrag(InventoryDragEvent event);
}