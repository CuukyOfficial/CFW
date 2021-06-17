package de.cuuky.cfw.inventory.inserter;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.ItemInserter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class AnimatedClosingInserter implements ItemInserter {

    private boolean cancelled, started;

    private boolean setItem(AdvancedInventory inventory, Map<Integer, ItemStack> items, Player player, int index, int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (cancelled)
            return false;

        this.setItem(inventory, index, items.get(index), false);
        player.updateInventory();
        return true;
    }

    @Override
    public void setItems(JavaPlugin plugin, AdvancedInventory inventory, Map<Integer, ItemStack> items, Player player, int size) {
        started = true;
        int delay = 900 / (inventory.getSize());
        int middle = (size - 1) / 2;

        new BukkitRunnable() {

            boolean end = false;

            @Override
            public void run() {
                for (int index = 0; index <= middle * 2; index++) {
                    int tempIndex = (int) Math.floor((float) index / 2);
                    if (!setItem(inventory, items, player, (end ? size - tempIndex - 1 : tempIndex), delay))
                        return;

                    end = !end;
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void stopInserting() {
        this.cancelled = true;
    }

    @Override
    public boolean hasStarted() {
        return this.started;
    }
}