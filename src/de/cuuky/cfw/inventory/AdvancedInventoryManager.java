package de.cuuky.cfw.inventory;

import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class AdvancedInventoryManager extends FrameworkManager {

    private final List<AdvancedInventory> inventories = new CopyOnWriteArrayList<>();

    public AdvancedInventoryManager(JavaPlugin ownerInstance) {
        super(FrameworkManagerType.ADVANCED_INVENTORY, ownerInstance);
        this.ownerInstance.getServer().getPluginManager().registerEvents(new AdvancedInventoryListener(this), ownerInstance);
    }

    protected AdvancedInventory registerInventory(AdvancedInventory inventory) {
        this.inventories.add(inventory);
        return inventory;
    }

    protected boolean unregisterInventory(AdvancedInventory inventory) {
        return this.inventories.remove(inventory);
    }

    public void updateInventories(AdvancedInventory self, Function<AdvancedInventory, Boolean> filter) {
        for (AdvancedInventory inventory : this.inventories)
            if ((self != null && !inventory.equals(self)) && filter.apply(inventory))
                inventory.update();
    }

    public void updateInventories(Function<AdvancedInventory, Boolean> filter) {
        this.updateInventories(null, filter);
    }

    @SafeVarargs
    public final void updateInventories(Class<? extends AdvancedInventory>... clazzes) {
        List<Class<? extends AdvancedInventory>> clazzList = Arrays.asList(clazzes);
        this.updateInventories(inv -> clazzList.contains(inv.getClass()));
    }

    public void closeInventories() {
        for (int i = this.inventories.size() - 1; i > -1; i--)
            this.inventories.get(i).close();
    }

    public List<AdvancedInventory> getInventories() {
        return inventories;
    }

    public AdvancedInventory getInventory(Player player) {
        return inventories.stream().filter(inv -> inv.getPlayer() != null && inv.getPlayer().equals(player)).findFirst().orElse(null);
    }

    public AdvancedInventory getInventory(Inventory inventory) {
        return inventories.stream().filter(inv -> inv.getInventory() != null && inv.getInventory().equals(inventory)).findFirst().orElse(null);
    }
}