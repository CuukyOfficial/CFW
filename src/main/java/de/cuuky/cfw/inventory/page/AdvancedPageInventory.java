package de.cuuky.cfw.inventory.page;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.EventNotifiable;
import de.cuuky.cfw.inventory.InfoProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.*;
import java.util.function.Supplier;

public abstract class AdvancedPageInventory extends AdvancedInventory implements EventNotifiable {

    private final Map<Integer, Supplier<Page<?>>> pages = new HashMap<>();
    private final Map<Integer, Page<?>> loaded = new HashMap<>();
    private int min, max;

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

    private Optional<Page<?>> getCurrentPage() {
        return Optional.ofNullable(this.getLoadedPage(this.getPage()));
    }

//    protected int getPageMax(int add) {
//        int page;
//        for (page = this.getStartPage(); this.pages.get(page) != null; page += add) continue;
//        return page + (add * -1);
//    }

    protected void registerPage(int page, Supplier<Page<?>> info) {
        this.pages.put(page, info);
        this.max = Math.max(max, page);
        this.min = Math.min(min,page);
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
        return page != null ? Collections.singletonList(page) : null;
    }

    @Override
    protected int getMaxPage() {
        return this.max;
    }

    @Override
    protected int getMinPage() {
        return this.min;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        this.getCurrentPage().ifPresent(p -> this.runOptionalEventNotification(p, e -> e.onInventoryClick(event)));
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        this.getCurrentPage().ifPresent(p -> this.runOptionalEventNotification(p, e -> e.onInventoryClose(event)));
    }

    @Override
    public void onInventoryDrag(InventoryDragEvent event) {
        this.getCurrentPage().ifPresent(p -> this.runOptionalEventNotification(p, e -> e.onInventoryDrag(event)));
    }

    @Override
    public void refreshContent() {
        this.getCurrentPage().ifPresent(Page::refreshContent);
    }
}