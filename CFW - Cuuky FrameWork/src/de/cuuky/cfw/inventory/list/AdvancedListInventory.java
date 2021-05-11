package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.ItemClick;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class AdvancedListInventory<T> extends AdvancedInventory {

    private final int size;
    private final List<T> list;

    public AdvancedListInventory(AdvancedInventoryManager manager, Player player, int size, List<T> list) {
        super(manager, player);

        this.size = size;
        this.list = list;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    protected void addListItem(int start, int index) {
        T item = this.list.get(start + index);
        this.addItem(index, this.getItemStack(item), this.getClick(item));
    }

    protected abstract ItemStack getItemStack(T item);

    protected abstract ItemClick getClick(T item);

    @Override
    public int getMaxPage() {
        return (int) Math.ceil((float) this.list.size() / (float) this.getUsableSize());
    }

    @Override
    protected void refreshContent() {
        int start = this.getUsableSize() * (this.getPage() - 1);
        for (int index = 0; index < this.getUsableSize() && start + index < list.size(); index++)
            this.addListItem(start, index);
    }

    protected List<T> getList() {
        return list;
    }
}
