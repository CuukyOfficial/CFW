package de.cuuky.cfw.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public interface ItemInserter {

    void setItems(JavaPlugin plugin, AdvancedInventory inventory, Map<Integer, ItemStack> items, Player player, int size);

    void stopInserting();

    boolean hasStarted();

    default void setItem(AdvancedInventory inventory, int index, ItemStack stack, boolean overwrite) {
        inventory.addToInventory(index, stack, overwrite);
    }
}