package de.cuuky.cfw.hooking.hooks.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.hooking.hooks.HookEntity;
import de.cuuky.cfw.hooking.hooks.HookEntityType;

public class ItemHook extends HookEntity {

	private final ItemHookHandler hookListener;
	private final int slot;
	private final ItemStack stack;
	private boolean dropable = false, dragable = false;

	public ItemHook(Player player, ItemStack stack, int slot, ItemHookHandler listener) {
		super(HookEntityType.ITEM, player);

		this.hookListener = listener;
		this.slot = slot;
		this.stack = stack;

		player.getInventory().setItem(slot, stack);
		player.updateInventory();
	}

	@Override
	protected HookEntity getExisting(HookManager manager, Player player) {
		return manager.getItemHook(stack, player);
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

	public int getSlot() {
		return slot;
	}
}