package de.cuuky.cfw.inventory.page;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.InventoryInfoProvider;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AdvancedPageInventory extends AdvancedInventory {

    private Map<Integer, Supplier<InventoryInfoProvider>> pages = new HashMap<>();
    private Map<Integer, InventoryInfoProvider> loaded = new HashMap<>();
    private boolean initialized;

    public AdvancedPageInventory(AdvancedInventoryManager manager, Player player) {
        super(manager, player);
    }

    private InventoryInfoProvider getLoadedPage(int page) {
        Supplier<InventoryInfoProvider> info;
        InventoryInfoProvider loaded = this.loaded.get(page);
        if (loaded == null && (info = this.pages.get(this.getPage())) != null)
            this.loaded.put(page, loaded = info.get());

        return loaded;
    }

    private <T> T getInfo(Function<InventoryInfoProvider, T> getter, Function<AdvancedPageInventory, T> fallback) {
        InventoryInfoProvider page = this.getLoadedPage(this.getPage());
        return page == null ? fallback.apply(this) : getter.apply(page);
    }

    private int getPageMax(int add) {
        int page;
        for (page = this.getStartPage(); pages.get(page) != null; page += add)
            continue;
        return page + (add * -1);
    }

    protected abstract int getDefaultSize();

    protected abstract String getDefaultTitle();

    protected void registerPage(int page, Supplier<InventoryInfoProvider> info) {
        this.pages.put(page, info);
        if (this.loaded.get(page) != null)
            this.loaded.put(page, info.get());
    }

    protected void registerPage(int page, Runnable runnable, Supplier<Integer> size, Supplier<String> title) {
        assert runnable != null;
        this.registerPage(page, () -> new PageInfo(runnable, size, title));
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
        String title = this.getInfo(InventoryInfoProvider::getTitle, AdvancedPageInventory::getDefaultTitle);
        return title == null ? this.getDefaultTitle() : title;
    }

    @Override
    public void refreshContent() {
        InventoryInfoProvider info = this.getLoadedPage(this.getPage());
        if (info != null)
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