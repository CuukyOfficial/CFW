package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public abstract class AdvancedAsyncListInventory<T> extends AdvancedListInventory<T> {

    public AdvancedAsyncListInventory(AdvancedInventoryManager manager, Player player, int size, List<T> list) {
        super(manager, player, size, list);
    }

    @Override
    protected void addListItem(int index, T item) {
        new BukkitRunnable() {
            @Override
            public void run() {
                AdvancedAsyncListInventory.super.addListItem(index, item);
            }
        }.runTaskLaterAsynchronously(this.getManager().getOwnerInstance(), 0L);
    }
}