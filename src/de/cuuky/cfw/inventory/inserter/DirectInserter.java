package de.cuuky.cfw.inventory.inserter;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.ItemInserter;

public class DirectInserter implements ItemInserter {

    private boolean started = false;

    @Override
    public void setItems(JavaPlugin plugin, AdvancedInventory inventory, Map<Integer, ItemStack> items, Player player, int size) {
        started = true;
        items.keySet().forEach(index -> this.setItem(inventory, index, items.get(index), false));
        player.updateInventory();
    }

    @Override
    public void stopInserting() {
    }

    @Override
    public boolean hasStarted() {
        return this.started;
    }
}