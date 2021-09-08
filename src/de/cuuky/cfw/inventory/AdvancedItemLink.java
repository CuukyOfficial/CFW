package de.cuuky.cfw.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

class AdvancedItemLink {

    private final ItemStack stack;
    private final ItemClick link;

    AdvancedItemLink(ItemStack stack, ItemClick click) {
        this.stack = stack;
        this.link = click;
    }

    ItemStack getStack() {
        return this.stack;
    }

    boolean run(InventoryClickEvent event) {
        if (this.link == null)
            return false;

        link.onItemClick(event);
        return true;
    }
}