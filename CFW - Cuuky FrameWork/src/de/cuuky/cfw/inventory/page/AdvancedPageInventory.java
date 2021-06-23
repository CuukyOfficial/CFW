package de.cuuky.cfw.inventory.page;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.InventoryInfoProvider;
import de.cuuky.cfw.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AdvancedPageInventory extends AdvancedInventory {

    private List<InventoryInfoProvider> pages = new ArrayList<>();

    public AdvancedPageInventory(AdvancedInventoryManager manager, Player player) {
        super(manager, player);

        this.registerPage(1, this::page1, 9, "Test1");
        this.registerPage(2, this::page2, 18, "Test2");
    }

    private void page1() {
        this.addItem(0, new ItemBuilder().material(Material.ARROW).build());
    }

    private void page2() {
        this.addItem(0, new ItemBuilder().material(Material.APPLE).build());
    }

    private <T> T getInfo(Function<InventoryInfoProvider, T> getter, Function<AdvancedPageInventory, T> fallback) {
        InventoryInfoProvider info;
        return (info = this.pages.get(this.getPage())) == null ? fallback.apply(this) : getter.apply(info);
    }

    private int getPageMax(int add) {
        int page;
        for (page = this.getStartPage(); pages.get(page) != null; page += add)
            continue;
        return page;
    }

    protected abstract int getDefaultSize();

    protected abstract String getDefaultTitle();

    protected void registerPage(int page, InventoryInfoProvider info) {
        this.pages.set(page, info);
    }

    protected void registerPage(int page, Runnable runnable, Supplier<Integer> size, Supplier<String> title) {
        assert runnable != null;
        this.registerPage(page, new PageInfo(runnable, size, title));
    }

    protected void registerPage(int page, Runnable runnable, int size, String title) {
        this.registerPage(page, runnable, () -> size, () -> title);
    }

    @Override
    public int getSize() {
        int size = this.getInfo(InventoryInfoProvider::getSize, AdvancedPageInventory::getDefaultSize);
        return size == 0 ? this.getDefaultSize() : size;
    }

    @Override
    public String getTitle() {
        String title = this.getInfo(InventoryInfoProvider::getTitle, AdvancedPageInventory::getTitle);
        return title == null ? this.getDefaultTitle() : title;
    }

    @Override
    public void refreshContent() {
        InventoryInfoProvider info;
        if ((info = this.pages.get(this.getPage())) != null)
            info.refreshContent();
    }

    @Override
    protected int getMaxPage() {
        return this.getPageMax(1);
    }

    @Override
    protected int getMinPage() {
        return this.getPageMax(-1);
    }
}