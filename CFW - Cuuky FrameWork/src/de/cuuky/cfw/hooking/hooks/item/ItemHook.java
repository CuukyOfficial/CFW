package de.cuuky.cfw.hooking.hooks.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.hooking.hooks.HookEntity;
import de.cuuky.cfw.hooking.hooks.HookEntityType;

public class ItemHook extends HookEntity {

	private ItemHookHandler hookListener;
	private boolean dropable, dragable;
	private ItemStack stack;

	public ItemHook(Player player, ItemStack stack, int slot, ItemHookHandler listener) {
		super(HookEntityType.ITEM, player);
		
		this.hookListener = listener;
		this.stack = stack;
		this.dropable = false;
		this.dragable = false;
		
		player.getInventory().setItem(slot, stack);
		
		player.updateInventory();
	}
	
	@Override
	public void setManager(HookManager manager) {
		if(manager.getItemHook(stack, player) != null)
			manager.getItemHook(stack, player).unregister();
		
		super.setManager(manager);
	}
	
	public void setDropable(boolean dropable) {
		this.dropable = dropable;
	}
	
	public boolean isDropable() {
		return dropable;
	}
	
	public void setDragable(boolean dragable) {
		this.dragable = dragable;
	}
	
	public boolean isDragable() {
		return dragable;
	}

	public ItemStack getItemStack() {
		return stack;
	}
	
	public ItemHookHandler getHookListener() {
		return hookListener;
	}
}