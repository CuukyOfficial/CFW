package de.cuuky.cfw.inventory.page;

import de.cuuky.cfw.inventory.InventoryInfoProvider;

import java.util.function.Supplier;

public class PageInfo implements InventoryInfoProvider {

    private Runnable runnable;
    private Supplier<Integer> size;
    private Supplier<String> title;

    public PageInfo(Runnable runnable, Supplier<Integer> size, Supplier<String> title) {
        this.runnable = runnable;
        this.size = size;
        this.title = title;
    }

    @Override
    public void refreshContent() {
        runnable.run();
    }

    @Override
    public int getSize() {
        return this.size == null ? 0 : this.size.get();
    }

    @Override
    public String getTitle() {
        return this.title == null ? null : this.title.get();
    }
}