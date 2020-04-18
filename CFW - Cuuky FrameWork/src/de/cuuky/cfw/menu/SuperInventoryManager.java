package de.cuuky.cfw.menu;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import de.cuuky.cfw.CuukyFrameWork;

public class SuperInventoryManager {
	
	private CuukyFrameWork instance;
	private ArrayList<SuperInventory> inventories;
	
	public SuperInventoryManager(CuukyFrameWork instance) {
		this.instance = instance;
		this.inventories = new ArrayList<>();
	}

	public CuukyFrameWork getInstance() {
		return instance;
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
		for(int i = this.inventories.size() - 1; i > -1; i--)
			this.inventories.get(i).close(true);
	}
	
	public ArrayList<SuperInventory> getInventories() {
		return inventories;
	}
	
	public SuperInventory getInventory(Player player) {
		for(SuperInventory inventory : inventories)
			if(inventory.getOpener().equals(player))
				return inventory;

		return null;
	}
}