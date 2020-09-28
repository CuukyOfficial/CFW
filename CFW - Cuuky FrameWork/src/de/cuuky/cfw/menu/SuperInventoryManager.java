package de.cuuky.cfw.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import de.cuuky.cfw.menu.utils.InventoryListener;

public class SuperInventoryManager extends FrameworkManager {

	private List<SuperInventory> inventories;

	public SuperInventoryManager(JavaPlugin instance) {
		super(FrameworkManagerType.INVENTORY, instance);

		this.inventories = new ArrayList<>();
		this.ownerInstance.getServer().getPluginManager().registerEvents(new InventoryListener(this), ownerInstance);
	}

	public void updateInventories(Class<? extends SuperInventory> type) {
		for (SuperInventory inventory : this.inventories)
			if (inventory.getClass().equals(type))
				inventory.updateInventory();
	}

	public SuperInventory registerInventory(SuperInventory inventory) {
		inventory.setManager(this);
		this.inventories.add(inventory);
		return inventory;
	}

	public boolean unregisterInventory(SuperInventory inventory) {
		return this.inventories.remove(inventory);
	}

	public void closeInventories() {
		for (int i = this.inventories.size() - 1; i > -1; i--)
			this.inventories.get(i).close(true);
	}

	public List<SuperInventory> getInventories() {
		return inventories;
	}

	public SuperInventory getInventory(Player player) {
		for (SuperInventory inventory : inventories)
			if (inventory.getOpener().equals(player))
				return inventory;

		return null;
	}
}