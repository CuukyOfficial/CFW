package de.cuuky.cfw.menu.utils;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryItemLink {

	private ItemStack stack;
	private int slot;
	private Runnable runnable;
	private ItemClickHandler clickHandler;

	public InventoryItemLink(ItemStack stack, int slot) {
		this.stack = stack;
		this.slot = slot;
	}

	public InventoryItemLink(ItemStack stack, int slot, Runnable runnable) {
		this(stack, slot);

		this.runnable = runnable;
	}

	public InventoryItemLink(ItemStack stack, int slot, ItemClickHandler clickHandler) {
		this(stack, slot);

		this.clickHandler = clickHandler;
	}

	public boolean isLink(ItemStack stack, int slot) {
		return this.stack.equals(stack) && this.slot == slot;
	}

	public void execute(InventoryClickEvent event) {
		if (runnable != null)
			runnable.run();

		if (clickHandler != null)
			clickHandler.onItemClick(event);
	}
}
