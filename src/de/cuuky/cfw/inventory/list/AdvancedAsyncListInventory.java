package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.utils.item.BuildItem;
import de.cuuky.cfw.version.types.Materials;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public abstract class AdvancedAsyncListInventory<T> extends AdvancedListInventory<T> {

    public AdvancedAsyncListInventory(AdvancedInventoryManager manager, Player player, List<T> list) {
        super(manager, player, list);
    }

    public AdvancedAsyncListInventory(AdvancedInventoryManager manager, Player player) {
        super(manager, player);
    }

    protected ItemStack getLoadingItem() {
        return new BuildItem().displayName("§cLoading...").material(Materials.COAL).build();
    }

    @Override
    protected void addListItem(int index, T item) {
        AdvancedAsyncListInventory.super.addItem(index, this.getLoadingItem());
        new BukkitRunnable() {
            @Override
            public void run() {
                AdvancedAsyncListInventory.super.addListItem(index, item);
            }
        }.runTaskLaterAsynchronously(this.getManager().getOwnerInstance(), 0L);
    }
}