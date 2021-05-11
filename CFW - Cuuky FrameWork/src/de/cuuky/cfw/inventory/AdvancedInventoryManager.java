package de.cuuky.cfw.inventory;

import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AdvancedInventoryManager extends FrameworkManager {

    private final List<AdvancedInventory> inventories = new CopyOnWriteArrayList<>();

    public AdvancedInventoryManager(JavaPlugin ownerInstance) {
        super(FrameworkManagerType.ADVANCED_INVENTORY, ownerInstance);
        this.ownerInstance.getServer().getPluginManager().registerEvents(new AdvancedInventoryListener(this), ownerInstance);
    }

    public void updateInventories(Class<? extends AdvancedInventory> type) {
        for (AdvancedInventory inventory : this.inventories)
            if (inventory.getClass().equals(type))
                inventory.update();
    }

    AdvancedInventory registerInventory(AdvancedInventory inventory) {
        this.inventories.add(inventory);
        return inventory;
    }

    boolean unregisterInventory(AdvancedInventory inventory) {
        return this.inventories.remove(inventory);
    }

    public void closeInventories() {
        for (int i = this.inventories.size() - 1; i > -1; i--)
            this.inventories.get(i).close();
    }

    public List<AdvancedInventory> getInventories() {
        return inventories;
    }

    public AdvancedInventory getInventory(Player player) {
        for (AdvancedInventory inventory : inventories)
            if (inventory.getPlayer().equals(player))
                return inventory;

        return null;
    }
}