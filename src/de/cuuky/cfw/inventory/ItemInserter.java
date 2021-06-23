package de.cuuky.cfw.inventory;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public interface ItemInserter {

    void setItems(JavaPlugin plugin, AdvancedInventory inventory, Map<Integer, ItemStack> items, Player player, int size);

    void stopInserting();

    boolean hasStarted();

    default void setItem(AdvancedInventory inventory, int index, ItemStack stack, boolean overwrite) {
        inventory.addToInventory(index, stack, overwrite);
    }
}