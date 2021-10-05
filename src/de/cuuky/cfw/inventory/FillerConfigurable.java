package de.cuuky.cfw.inventory;

import org.bukkit.inventory.ItemStack;

public interface FillerConfigurable {

    boolean shallInsertFiller(int location, ItemStack stack);
}