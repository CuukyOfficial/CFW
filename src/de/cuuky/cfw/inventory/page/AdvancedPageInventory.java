package de.cuuky.cfw.inventory.page;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.InfoProvider;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AdvancedPageInventory extends AdvancedInventory {

    private final Map<Integer, Supplier<Contentable>> pages = new HashMap<>();
    private final Map<Integer, Contentable> loaded = new HashMap<>();

    public AdvancedPageInventory(AdvancedInventoryManager manager, Player player) {
        super(manager, player);
    }

    private Contentable getLoadedPage(int page) {
        Supplier<Contentable> info;
        Contentable loaded = this.loaded.get(page);
        if (loaded == null && (info = this.pages.get(this.getPage())) != null)
            this.loaded.put(page, loaded = info.get());

        return loaded;
    }

    private <T> T getInfo(Function<Contentable, T> getter, Function<AdvancedPageInventory, T> fallback) {
        Contentable page = this.getLoadedPage(this.getPage());
        return page == null ? fallback.apply(this) : getter.apply(page);
    }

    private int getPageMax(int add) {
        int page;
        for (page = this.getStartPage(); pages.get(page) != null; page += add) continue;
        return page + (add * -1);
    }

    protected void registerPage(int page, Supplier<Contentable> info) {
        this.pages.put(page, info);
        if (this.loaded.get(page) != null) this.loaded.put(page, info.get());
    }

    protected void registerPage(int page, Runnable runnable, Supplier<Integer> size, Supplier<String> title) {
        assert runnable != null;
        this.registerPage(page, () -> new PageInfo(runnable, size, title));
    }

    protected void registerPage(int page, Runnable runnable, int size, String title) {
        this.registerPage(page, runnable, () -> size, () -> title);
    }

    @Override
    protected final List<InfoProvider> getCurrentProvider() {
        InfoProvider page = this.getLoadedPage(this.getPage());
        return page != null ? Arrays.asList(page) : null;
    }

    @Override
    public void refreshContent() {
        Contentable info = this.getLoadedPage(this.getPage());
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