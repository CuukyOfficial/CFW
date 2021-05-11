package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public abstract class AdvancedAsyncListInventory<T> extends AdvancedListInventory<T> {

    public AdvancedAsyncListInventory(AdvancedInventoryManager manager, int size, Player player, List<T> list) {
        super(manager, size, player, list);
    }

    @Override
    protected void addListItem(int start, int index) {
        new BukkitRunnable() {
            @Override
            public void run() {
                AdvancedAsyncListInventory.super.addListItem(start, index);
            }
        }.runTaskLaterAsynchronously(this.getManager().getOwnerInstance(), 0L);
    }
}