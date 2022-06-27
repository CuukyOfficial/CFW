/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.varoplugin.cfw.inventory.page;

import de.varoplugin.cfw.inventory.AdvancedInventory;
import de.varoplugin.cfw.inventory.AdvancedInventoryManager;
import de.varoplugin.cfw.inventory.EventNotifiable;
import de.varoplugin.cfw.inventory.InfoProvider;
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

    protected void registerPage(int page, Supplier<Page<?>> info) {
        this.pages.put(page, info);
        this.max = Math.max(max, page);
        this.min = Math.min(min, page);
        if (this.loaded.get(page) != null)
            this.loaded.put(page, info.get());
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
