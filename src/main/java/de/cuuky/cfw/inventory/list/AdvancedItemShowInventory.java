package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.inventory.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class AdvancedItemShowInventory extends AdvancedInventory implements FillerConfigurable {

    public AdvancedItemShowInventory(AdvancedInventoryManager manager, Player player) {
        super(manager, player);
    }

    protected void addShowItem(int i, ItemStack stack, ItemClick click) {
        this.addItem(i, stack, click);
    }

    protected int getIndex(int page) {
        return this.getUsableSize() * (page - 1);
    }

    protected int getCurrentIndex() {
        return this.getIndex(this.getPage());
    }

    protected abstract Map.Entry<ItemStack, ItemClick> getInfo(int index);

    protected int getRecommendedSize() {
        return Math.min(this.toInvSize(this.getMinSize()) + this.getInfo(Info.HOTBAR_SIZE), 54);
    }

    protected int getMinSize() {
        return 27;
    }

    @Override
    public int getSize() {
        return this.getRecommendedSize();
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