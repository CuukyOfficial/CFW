package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.ItemClick;
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

    protected ItemStack getLoadingItem() {
        return new BuildItem().displayName("§cLoading...").material(Materials.COAL).build();
    }

    @Override
    protected void addShowItem(int index, ItemStack item, ItemClick click) {
        if (item == null) return;
        super.addItem(index, this.getLoadingItem());
        new BukkitRunnable() {
            @Override
            public void run() {
                AdvancedAsyncListInventory.super.addShowItem(index, item, click);
                AdvancedAsyncListInventory.this.getPlayer().updateInventory();
            }
        }.runTaskLaterAsynchronously(this.getManager().getOwnerInstance(), 0L);
    }
}