package de.cuuky.cfw.inventory;

import de.cuuky.cfw.inventory.inserter.DirectInserter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AdvancedInventory extends InfoProviderHolder implements ContentRefreshable {

    private final Map<Supplier<ItemInfo>, Supplier<ItemClick>> selectors = new HashMap<Supplier<ItemInfo>, Supplier<ItemClick>>() {{
        put(() -> AdvancedInventory.this.getInfo(Info.BACKWARDS_INFO), () -> generateNavigator(AdvancedInventory.this::getMinPage, -1));
        put(() -> AdvancedInventory.this.getInfo(Info.FORWARDS_INFO), () -> generateNavigator(AdvancedInventory.this::getMaxPage, 1));
        put(() -> {
            if (AdvancedInventory.this.previous == null) return null;
            else return AdvancedInventory.this.getInfo(Info.BACK_INFO);
        }, () -> event -> AdvancedInventory.this.back());
        put(() -> AdvancedInventory.this.getInfo(Info.CLOSE_INFO), () -> event -> close());
    }};

    private AdvancedInventory previous;

    private final AdvancedInventoryManager manager;
    private final Player player;
    private int page = this.getStartPage(), interval = -1, lastClickedSlot;
    private Inventory inventory;
    private ItemInserter inserter;
    private boolean open;
    private BukkitTask autoTask;

    private final Map<Integer, AdvancedItemLink> items = new HashMap<>();

    public AdvancedInventory(AdvancedInventoryManager manager, Player player) {
        this.manager = manager;
        this.player = player;
        this.inserter = new DirectInserter();

        this.open();
    }

    private void createInventory() {
        String title = this.getInfo(Info.TITLE);
        title = title.length() > 32 ? title.substring(0, 32) : title;
        this.inventory = this.manager.getOwnerInstance().getServer().createInventory(this.player, this.getInfo(Info.SIZE), title);
    }

    private void validatePage() {
        this.page = Math.min(Math.max(this.page, this.getMinPage()), this.getMaxPage());
    }

    private ItemClick generateNavigator(Supplier<Integer> maxSup, int add) {
        int max = maxSup.get();
        if ((add < 0 && this.page <= max) || (add > 0 && this.page >= max))
            return null;

        return event -> this.setPage(this.page + add);
    }

    private Map<Integer, ItemStack> getContent(int size) {
        Map<Integer, ItemStack> stacks = new HashMap<>();
        for (int i = 0; i < size; i++) {
            AdvancedItemLink link = this.items.get(i);
            if (link == null) {
                ItemStack filler = this.getInfo(Info.FILLER_STACK);
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
            if ((click = this.selectors.get(infoSupplier).get()) == null
                    || (info = infoSupplier.get()) == null)
                continue;

            int realIndex = info.getIndex();
            this.items.put(realIndex, new AdvancedItemLink(info.getStack(), click));
            this.addToInventory(realIndex, info.getStack(), true);
        }
    }

    private boolean needsOpen() {
        InventoryView view = player.getOpenInventory();
        AdvancedInventory ai = this.manager.getInventory(view.getTopInventory());
        if (ai == null || !(view.getTitle().equals(ai.getInfo(Info.TITLE))
                && view.getTopInventory().getSize() == ai.getInfo(Info.SIZE))) {
            this.createInventory();
            return true;
        } else this.inventory = view.getTopInventory();

        this.inventory.clear();
        return false;
    }

    private void startTask() {
        if (this.interval == -1)
            return;

        this.autoTask = new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTaskTimerAsynchronously(this.getManager().getOwnerInstance(), interval, interval);
    }

    void slotClicked(int slot, InventoryClickEvent event) {
        this.lastClickedSlot = slot;
        if (this.getInfo(Info.CANCEL_CLICK)) event.setCancelled(true);
        if (this instanceof EventNotifiable) ((EventNotifiable) this).onInventoryClick(event);
        AdvancedItemLink link = this.items.get(slot);
        if (link == null) return;
        if (link.run(event) && this.open) this.update();
        this.playSound();
    }

    void inventoryClosed(InventoryCloseEvent event) {
        if (this instanceof EventNotifiable)
            ((EventNotifiable) this).onInventoryClose(event);

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

    @Override
    AdvancedInventory getUser() {
        return this;
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

    protected int calculateInvSize(int entries) {
        int mod = entries % 9;
        int result = (mod != 0 ? (9 - mod) : mod) + entries;
        return Math.min(result, 54);
    }

    protected void playSound() {
        Consumer<Player> soundPlayer = this.getInfo(Info.PLAY_SOUND);
        if (soundPlayer != null) soundPlayer.accept(getPlayer());
    }

    protected void updateOthers(Function<AdvancedInventory, Boolean> filter) {
        this.manager.updateInventories(this, filter);
    }

    protected void back() {
        this.close(false);
        if (previous != null)
            previous.open();
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
        return (int) Math.floor(this.getUsableSize() / 2);
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

        manager.registerInventory(this);
        new BukkitRunnable() {

            @Override
            public void run() {
                startTask();
                update();
            }
        }.runTaskLater(manager.getOwnerInstance(), 0L);
    }

    public void close(boolean close) {
        if (autoTask != null) {
            this.autoTask.cancel();
            autoTask = null;
        }

        this.inserter.stopInserting();
        if (close)
            player.closeInventory();

        this.open = false;
        this.manager.unregisterInventory(this);
    }

    public void close() {
        this.close(true);
    }

    public void update() {
        this.updateProvider();
        this.validatePage();
        boolean needsOpen = this.needsOpen();
        this.inserter.stopInserting();
        this.items.clear();
        this.setSelector();

        this.inserter = this.getInfo(Info.DO_ANIMATION) ? this.getInfo(Info.ITEM_INSERTER) : new DirectInserter();
        this.refreshContent();

        int size = this.getInfo(Info.SIZE);
        this.inserter.setItems(manager.getOwnerInstance(), this, this.getContent(size), player, size);

        if (!needsOpen)
            this.player.updateInventory();
        else
            this.player.openInventory(this.inventory);

        this.open = true;
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

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public AdvancedInventoryManager getManager() {
        return this.manager;
    }
}