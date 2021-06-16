package de.cuuky.cfw.inventory;

import org.bukkit.inventory.ItemStack;

public class ItemInfo {

    private int index;
    private ItemStack stack;

    public ItemInfo(int index, ItemStack stack) {
        this.index = index;
        this.stack = stack;
    }

    public ItemInfo setIndex(int index) {
        this.index = index;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public ItemInfo setStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemStack getStack() {
        return stack;
    }
}