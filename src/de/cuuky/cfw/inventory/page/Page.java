package de.cuuky.cfw.inventory.page;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.ContentRefreshable;
import de.cuuky.cfw.inventory.InfoProvider;
import de.cuuky.cfw.inventory.ItemClick;
import org.bukkit.inventory.ItemStack;

public interface Page<Inventory extends AdvancedInventory> extends InfoProvider, ContentRefreshable {

    Inventory getInventory();

    void refreshContent();

    @Override
    default int getPriority() {
        return 1;
    }

    default void addItem(int index, ItemStack stack, ItemClick click) {
        this.getInventory().addItem(index, stack, click);
    }

    default void addItem(int index, ItemStack stack) {
        this.getInventory().addItem(index, stack, null);
    }
}