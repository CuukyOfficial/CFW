package de.cuuky.cfw.inventory.page;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.InventoryInfoProvider;
import de.cuuky.cfw.inventory.ItemClick;
import org.bukkit.inventory.ItemStack;

public interface InventoryPage<Inventory extends AdvancedInventory> extends InventoryInfoProvider {

    Inventory getInventory();

    default void addItem(int index, ItemStack stack, ItemClick click) {
        this.getInventory().addItem(index, stack, click);
    }

    default void addItem(int index, ItemStack stack) {
        this.getInventory().addItem(index, stack, null);
    }
}