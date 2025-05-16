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

package de.varoplugin.cfw.inventory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.cryptomorin.xseries.inventory.BukkitInventoryView;
import com.cryptomorin.xseries.inventory.XInventoryView;

import de.varoplugin.cfw.inventory.inserter.DirectInserter;

public abstract class AdvancedInventory extends InfoProviderHolder implements ContentRefreshable, DefaultInfoProvider {

    private final Map<Supplier<ItemInfo>, Supplier<ItemClick>> selectors = new HashMap<Supplier<ItemInfo>, Supplier<ItemClick>>() {

        private static final long serialVersionUID = 1626277940088516063L;

        {
            this.put(() -> AdvancedInventory.this.getInfo(Info.BACKWARDS_INFO), () -> AdvancedInventory.this.generateNavigator(AdvancedInventory.this::getCachedMin, -1));
            this.put(() -> AdvancedInventory.this.getInfo(Info.FORWARDS_INFO), () -> AdvancedInventory.this.generateNavigator(AdvancedInventory.this::getCachedMax, 1));
            this.put(() -> {
                if (AdvancedInventory.this.previous == null)
                    return null;
                return AdvancedInventory.this.getInfo(Info.BACK_INFO);
            }, () -> event -> AdvancedInventory.this.back());
            this.put(() -> AdvancedInventory.this.getInfo(Info.CLOSE_INFO), () -> event -> AdvancedInventory.this.close());
        }
    };

    AdvancedInventory previous;

    private final AdvancedInventoryManager manager;
    private final Player player;
    private int page = this.getStartPage(), minPage = 1, maxPage = 1, interval = -1, lastClickedSlot = -1;
    private Inventory inventory;
    private ItemInserter inserter;
    private boolean open, updating;
    private BukkitTask autoTask;

    private final Map<Integer, AdvancedItemLink> items = new ConcurrentHashMap<>();
    private final List<PrioritisedInfo> infos = new LinkedList<>();

    public AdvancedInventory(AdvancedInventoryManager manager, Player player) {
        this.manager = manager;
        this.player = player;
        this.inserter = new DirectInserter();
        this.addProvider("Inventory", this);

        this.open();
    }

    int getCachedMin() {
        return this.minPage;
    }

    int getCachedMax() {
        return this.maxPage;
    }

    private void createInventory() {
        String title = this.getInfo(Info.TITLE);
        title = title.length() > 32 ? title.substring(0, 32) : title;
        this.inventory = this.manager.getPlugin().getServer().createInventory(this.player, this.getInfo(Info.SIZE), title);
    }

    private void validatePage() {
        this.page = Math.min(Math.max(this.page, this.minPage), this.maxPage);
    }

    ItemClick generateNavigator(Supplier<Integer> maxSup, int add) {
        int max = maxSup.get();
        if ((add < 0 && this.page <= max) || (add > 0 && this.page >= max))
            return null;
        return event -> this.setPage(this.page + add);
    }

    private ItemStack getFiller(int location) {
        ItemStack filler = this.getInfo(Info.FILLER_STACK);
        if (filler != null && this instanceof FillerConfigurable)
            if (!((FillerConfigurable) this).shallInsertFiller(location, filler))
                return null;

        return filler;
    }

    private Map<Integer, ItemStack> getContent(int size) {
        Map<Integer, ItemStack> stacks = new HashMap<>();
        for (int i = 0; i < size; i++) {
            AdvancedItemLink link = this.items.get(i);
            if (link == null) {
                ItemStack filler = this.getFiller(i);
                if (filler != null)
                    stacks.put(i, filler);
            } else
                stacks.put(i, link.getStack());
        }

        return stacks;
    }

    private void setSelector() {
        if (this.getInfo(Info.HOTBAR_SIZE) == 0)
            return;

        for (Supplier<ItemInfo> infoSupplier : this.selectors.keySet()) {
            ItemInfo info;
            ItemClick click;
            if ((click = this.selectors.get(infoSupplier).get()) == null || (info = infoSupplier.get()) == null)
                continue;

            int realIndex = info.getIndex();
            this.items.put(realIndex, new AdvancedItemLink(info.getStack(), click));
            this.addToInventory(realIndex, info.getStack(), true);
        }
    }

    private boolean needsOpen() {
        BukkitInventoryView view = XInventoryView.of(this.player.getOpenInventory());
        AdvancedInventory ai = this.manager.getInventory(view.getTopInventory());
        if (ai == null || !(view.getTitle().equals(ai.getInfo(Info.TITLE)) && view.getTopInventory().getSize() == ai.getInfo(Info.SIZE))) {
            this.createInventory();
            return true;
        }

        this.inventory = view.getTopInventory();
        this.inventory.clear();
        return false;
    }

    void startTask() {
        if (this.interval == -1)
            return;

        this.autoTask = new BukkitRunnable() {
            @Override
            public void run() {
                AdvancedInventory.this.update();
            }
        }.runTaskTimer(this.getManager().getPlugin(), this.interval, this.interval);
    }

    private void updateInformation() {
        this.minPage = this.getMinPage();
        this.maxPage = this.getMaxPage();
    }

    void slotClicked(int slot, InventoryClickEvent event) {
        this.lastClickedSlot = slot;
        AdvancedItemLink link = this.items.get(slot);
        if (link != null)
            link.run(event);
        this.updateProvider();
        this.playSound();
        if (this.open && (link != null && link.hasLink()))
            this.update();
        if ((link == null && this.getFiller(slot) != null) || this.getInfo(Info.CANCEL_CLICK))
            event.setCancelled(true);
    }

    void inventoryClosed() {
        this.close(false);
    }

    void addToInventory(int index, ItemStack stack, boolean overwrite) {
        if (!overwrite && this.getInventory().getItem(index) != null)
            return;
        this.inventory.setItem(index, stack);
    }

    void setPrevious(AdvancedInventory previous) {
        this.previous = previous;
    }

    protected int getMaxPage() {
        return 1;
    }

    protected int getMinPage() {
        return 1;
    }

    protected int getStartPage() {
        return 1;
    }

    protected void runOptionalEventNotification(Object object, Consumer<EventNotifiable> notifiableConsumer) {
        if (object instanceof EventNotifiable)
            notifiableConsumer.accept((EventNotifiable) object);
    }

    protected void runOptionalInventoryNotification(Object object, Consumer<InventoryNotifiable> notifiableConsumer) {
        if (object instanceof InventoryNotifiable)
            notifiableConsumer.accept((InventoryNotifiable) object);
    }

    protected int toInvSize(int entries) {
        int mod = entries % 9;
        int result = (mod != 0 ? (9 - mod) : mod) + entries;
        return Math.min(result, 54);
    }

    protected boolean clickedContent(int slot) {
        return slot < this.getUsableSize() && slot >= 0;
    }

    protected boolean clickedContent() {
        return this.clickedContent(this.getLastClickedSlot());
    }

    protected void addInfo(PrioritisedInfo info) {
        this.infos.add(info);
    }

    @Override
    public final List<PrioritisedInfo> getPrioritisedInfos() {
        return this.infos;
    }

    protected void playSound() {
        Consumer<Player> soundPlayer = this.getInfo(Info.PLAY_SOUND);
        if (soundPlayer != null)
            soundPlayer.accept(this.getPlayer());
    }

    protected void updateOthers(Function<AdvancedInventory, Boolean> filter) {
        this.manager.updateInventories(this, filter);
    }

    protected void back() {
        this.close(true);
        if (this.previous != null)
            this.previous.open();
    }

    protected void setAutoRefresh(int interval) {
        this.interval = interval;
    }

    protected int getPage() {
        return this.page;
    }

    protected void setPage(int page) {
        this.page = page;
    }

    protected int getCenter() {
        return (int) Math.floor((float) this.getUsableSize() / 2);
    }

    protected int getUsableSize() {
        return this.getInfo(Info.SIZE) - this.getInfo(Info.HOTBAR_SIZE);
    }

    protected AdvancedInventory getPrevious() {
        return this.previous;
    }

    protected boolean isOpen() {
        return this.open;
    }

    protected int getLastClickedSlot() {
        return this.lastClickedSlot;
    }

    public void open() {
        if (this.open)
            throw new IllegalStateException("Cannot reopen already opened inventory");
        this.manager.registerInventory(this);
        new BukkitRunnable() {

            @Override
            public void run() {
                AdvancedInventory.this.startTask();
                AdvancedInventory.this.updateProvider();
                AdvancedInventory.this.update();
            }
        }.runTaskLater(this.manager.getPlugin(), 0L);
    }

    public void close(boolean close) {
        if (this.autoTask != null) {
            this.autoTask.cancel();
            this.autoTask = null;
        }

        this.inserter.stopInserting();
        this.runOptionalInventoryNotification(this, InventoryNotifiable::onClose);
        if (close)
            this.player.closeInventory();
        this.open = false;
        this.manager.unregisterInventory(this);
    }

    public void close() {
        this.close(true);
    }

    public void update() {
        this.updating = true;
        this.validatePage();
        boolean needsOpen = this.needsOpen();
        this.inserter.stopInserting();
        this.items.clear();

        this.inserter = this.getInfo(Info.DO_ANIMATION) ? this.getInfo(Info.ITEM_INSERTER) : new DirectInserter();
        this.refreshContent();

        this.updateInformation();
        this.setSelector();
        int size = this.getInfo(Info.SIZE);
        this.inserter.setItems(this.manager.getPlugin(), this, this.getContent(size), this.player, size);
        if (!needsOpen)
            this.player.updateInventory();
        else
            this.player.openInventory(this.inventory);

        this.open = true;
        this.updating = false;
    }

    public void addItem(int index, ItemStack stack, ItemClick click) {
        this.items.put(index, new AdvancedItemLink(stack, click));
        if (this.inserter.hasStarted())
            this.addToInventory(index, stack, true);
    }

    public void addItem(int index, ItemStack stack) {
        this.addItem(index, stack, null);
    }

    public void openNext(AdvancedInventory inventory) {
        if (this.previous != inventory)
            inventory.setPrevious(this);
        this.close(false);
    }

    @Override
    public AdvancedInventory getUser() {
        return this;
    }

    @Override
    public final List<Info<?>> getProvidedInfos() {
        return Info.values();
    }

    public boolean isUpdating() {
        return this.updating;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public AdvancedInventoryManager getManager() {
        return this.manager;
    }
}
