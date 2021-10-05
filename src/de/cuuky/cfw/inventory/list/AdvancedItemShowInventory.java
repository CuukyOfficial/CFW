package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.inventory.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

abstract class AdvancedItemShowInventory extends AdvancedInventory implements FillerConfigurable {

    public AdvancedItemShowInventory(AdvancedInventoryManager manager, Player player) {
        super(manager, player);
    }

    protected void addShowItem(int i, ItemStack stack, ItemClick click) {
        this.addItem(i, stack, click);
    }

    protected int getCurrentIndex() {
        return this.getUsableSize() * (this.getPage() - 1);
    }

    protected abstract Map.Entry<ItemStack, ItemClick> getInfo(int index);

    protected int getRecommendedSize(int min, int max) {
        int size = this.calculateInvSize(this.getMinPageSize());
        return Math.min(Math.max(Math.min(size, max), min) + this.getInfo(Info.HOTBAR_SIZE), 54);
    }

    protected int getRecommendedSize(int min) {
        return this.getRecommendedSize(min, 54);
    }

    protected int getRecommendedSize() {
        return this.getRecommendedSize(27);
    }

    @Override
    protected final int getStartPage() {
        return 1;
    }

    @Override
    protected final int getMinPage() {
        return 1;
    }

    @Override
    public abstract int getMaxPage();

    protected int getMinPageSize() {
        return 27;
    }

    @Override
    public int getSize() {
        return this.getRecommendedSize();
    }

    @Override
    public boolean shallInsertFiller(int location, ItemStack stack) {
        return location >= this.getUsableSize();
    }

    @Override
    public void refreshContent() {
        int start = this.getCurrentIndex(), usable = this.getUsableSize();
        for (int i = 0; i < usable; i++) {
            Map.Entry<ItemStack, ItemClick> info = this.getInfo(i + start);
            if (info == null) continue;
            this.addShowItem(i, info.getKey(), info.getValue());
        }
    }
}