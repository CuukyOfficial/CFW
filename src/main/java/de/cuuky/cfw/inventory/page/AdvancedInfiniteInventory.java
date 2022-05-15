package de.cuuky.cfw.inventory.page;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.ItemClick;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AdvancedInfiniteInventory extends AdvancedInventory {

    private int max = 1;

    public AdvancedInfiniteInventory(AdvancedInventoryManager manager, Player player) {
        super(manager, player);
    }

    private void checkMax(int test) {
        if (test > max)
            max = test;
    }

    protected int getIndex() {
        return (this.getPage() - 1) * this.getUsableSize();
    }

    private int convertIndex(int index) {
        return index - this.getIndex();
    }

    @Override
    protected final int getMaxPage() {
        return (int) Math.ceil((double) max / (double) this.getUsableSize());
    }

    @Override
    public final void addItem(int index, ItemStack stack) {
        this.addItem(index, stack, null);
    }

    @Override
    public final void addItem(int index, ItemStack stack, ItemClick click) {
        this.checkMax(index);
        index = this.convertIndex(index);
        if (0 <= index && index < this.getUsableSize())
            super.addItem(index, stack, click);
    }
}