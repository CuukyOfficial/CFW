package de.cuuky.cfw.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public interface EventNotifiable {

    void onInventoryClick(InventoryClickEvent event);

    void onInventoryClose(InventoryCloseEvent event);
}