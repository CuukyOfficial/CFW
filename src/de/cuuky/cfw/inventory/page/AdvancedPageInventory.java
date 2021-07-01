package de.cuuky.cfw.inventory.page;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.InfoProvider;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AdvancedPageInventory extends AdvancedInventory {

    private final Map<Integer, Supplier<Page<?>>> pages = new HashMap<>();
    private final Map<Integer, Page<?>> loaded = new HashMap<>();

    public AdvancedPageInventory(AdvancedInventoryManager manager, Player player) {
        super(manager, player);
    }

    private Page<?> getLoadedPage(int page) {
        Supplier<Page<?>> info;
        Page<?> loaded = this.loaded.get(page);
        if (loaded == null && (info = this.pages.get(this.getPage())) != null)
            this.loaded.put(page, loaded = info.get());

        return loaded;
    }

    protected int getPageMax(int add) {
        int page;
        for (page = this.getStartPage(); pages.get(page) != null; page += add) continue;
        return page + (add * -1);
    }

    protected void registerPage(int page, Supplier<Page<?>> info) {
        this.pages.put(page, info);
        if (this.loaded.get(page) != null) this.loaded.put(page, info.get());
    }

    protected void registerPage(int page, Runnable runnable, Supplier<Integer> size, Supplier<String> title) {
        assert runnable != null;
        this.registerPage(page, () -> new VirtualPage(runnable, size, title));
    }

    protected void registerPage(int page, Runnable runnable, int size, String title) {
        this.registerPage(page, runnable, () -> size, () -> title);
    }

    @Override
    protected final List<InfoProvider> getTemporaryProvider() {
        InfoProvider page = this.getLoadedPage(this.getPage());
        return page != null ? Arrays.asList(page) : null;
    }

    @Override
    public void refreshContent() {
        Page<?> info = this.getLoadedPage(this.getPage());
        if (info != null) info.refreshContent();
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