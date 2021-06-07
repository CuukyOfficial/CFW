package de.cuuky.cfw.inventory;

import de.cuuky.cfw.inventory.inserter.DirectInserter;
import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.cfw.version.types.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AdvancedInventory {

    private final Map<Supplier<ItemInfo>, Supplier<ItemClick>> selectors = new HashMap<Supplier<ItemInfo>, Supplier<ItemClick>>() {{
        put(AdvancedInventory.this::getBackwardsInfo, () -> generateNavigator(() -> 1, -1));
        put(AdvancedInventory.this::getForwardsInfo, () -> generateNavigator(AdvancedInventory.this::getMaxPage, 1));
        put(() -> {
            if (AdvancedInventory.this.previous == null)
                return null;

            return AdvancedInventory.this.getBackInfo();
        }, () -> event -> AdvancedInventory.this.back());
        put(AdvancedInventory.this::getCloseInfo, () -> event -> close());
    }};

    private AdvancedInventory previous;

    private final AdvancedInventoryManager manager;
    private final Player player;
    private int page = 1, interval = -1;
    private Inventory inventory;
    private ItemInserter inserter;
    private boolean selectorsEnabled, open;
    private BukkitTask autoTask;

    private final Map<Integer, AdvancedItemLink> items = new HashMap<>();

    public AdvancedInventory(AdvancedInventoryManager manager, Player player) {
        this.manager = manager;
        this.player = player;
        this.inserter = this.getInserter();

        this.open();
    }

    private void createInventory() {
        this.inventory = this.manager.getOwnerInstance().getServer().createInventory(this.player, this.getSize(), this.getTitle());
    }

    private int convertPage(int toGo) {
        int max = this.getMaxPage();
        return Math.min(toGo, max);
    }

    private ItemClick generateNavigator(Supplier<Integer> maxSup, int add) {
        int max = maxSup.get();
        if ((add < 0 && this.page <= max) || (add > 0 && this.page >= max))
            return null;

        return event -> this.page = this.convertPage(this.page + add);
    }

    private Map<Integer, ItemStack> getContent(int size) {
        Map<Integer, ItemStack> stacks = new HashMap<>();
        for (int i = 0; i < size; i++) {
            AdvancedItemLink link = this.items.get(i);
            if (link == null) {
                ItemStack filler = this.getFillerStack();
                if (filler != null)
                    stacks.put(i, filler);
            } else
                stacks.put(i, link.getStack());
        }

        return stacks;
    }

    private void setSelector() {
        for (Supplier<ItemInfo> infoSupplier : this.selectors.keySet()) {
            ItemInfo info;
            ItemClick click;
            if ((click = this.selectors.get(infoSupplier).get()) == null || (info = infoSupplier.get()) == null)
                continue;

            int realIndex = info.getIndex();
            this.items.put(realIndex, new AdvancedItemLink(info.getStack(), click));
            this.addToInventory(realIndex, info.getStack(), false);
        }
    }

    private boolean needsOpen() {
        Inventory inv = player.getOpenInventory().getTopInventory();
        if (inv == null || !(this.getTitle().equals(inv.getTitle()) && this.getSize() == inv.getSize())) {
            this.createInventory();
            return true;
        } else this.inventory = inv;

        this.inventory.clear();
        return false;
    }

    private void startTask() {
        if (interval == -1)
            return;

        this.autoTask = new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTaskTimerAsynchronously(this.getManager().getOwnerInstance(), interval, interval);
    }

    void slotClicked(int slot, InventoryClickEvent event) {
        AdvancedItemLink link = this.items.get(slot);
        if (link == null)
            return;

        this.playSound();
        if (this instanceof InventoryNotifiable)
            ((InventoryNotifiable) this).onInventoryClick(event);

        if (link.run(event) && this.open)
            this.update();
    }

    void inventoryClosed(InventoryCloseEvent event) {
        if (this instanceof InventoryNotifiable)
            ((InventoryNotifiable) this).onInventoryClose(event);

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

    Inventory getInventory() {
        return inventory;
    }

    protected abstract String getTitle();

    protected abstract void refreshContent();

    protected int getMaxPage() {
        return 1;
    }

    protected void playSound() {
        this.player.playSound(player.getLocation(), Sounds.CLICK.bukkitSound(), 1, 1);
    }

    protected void updateOthers(Function<AdvancedInventory, Boolean> filter) {
        this.manager.updateInventories(this, filter);
    }

    protected void back() {
        close(false);
        previous.open();
    }

    protected boolean hasSelectors() {
        return (selectorsEnabled = this.selectors.keySet().stream().anyMatch(i -> i.get() != null && selectors.get(i) != null));
    }

    private String getPageViewer() {
        return "§7" + this.getPage() + "§8/§7" + this.getMaxPage();
    }

    protected ItemInfo getBackwardsInfo() {
        return new ItemInfo(this.getUsableSize(), new ItemBuilder().material(Material.ARROW).displayname("§cBackwards " + getPageViewer()).build());
    }

    protected ItemInfo getForwardsInfo() {
        return new ItemInfo(this.getUsableSize() + 8, new ItemBuilder().material(Material.ARROW).displayname("§aForwards " + getPageViewer()).build());
    }

    protected ItemInfo getCloseInfo() {
        return new ItemInfo(this.getUsableSize() + 4, new ItemBuilder().material(Materials.REDSTONE.parseMaterial()).displayname("§4Close").build());
    }

    protected ItemInfo getBackInfo() {
        return new ItemInfo(this.getUsableSize() + 3, new ItemBuilder().material(Material.STONE_BUTTON).displayname("§fBack to '" + this.previous.getTitle() + "§f'").build());
    }

    protected ItemStack getFillerStack() {
        return new ItemBuilder().displayname("§c").itemstack(new ItemStack(Materials.BLACK_STAINED_GLASS_PANE.parseMaterial(), 1, (short) 15)).build();
    }

    protected void addItem(int index, ItemStack stack, ItemClick click) {
        this.items.put(index, new AdvancedItemLink(stack, click));
        if (this.inserter.hasStarted())
            this.addToInventory(index, stack, true);
    }

    protected void addItem(int index, ItemStack stack) {
        this.addItem(index, stack, null);
    }

    protected void setAutoRefresh(int interval) {
        this.interval = interval;
    }

    protected void openNext(AdvancedInventory inventory) {
        if (this.previous != inventory)
            inventory.setPrevious(this);
        this.close(false);
    }

    protected ItemInserter getInserter() {
        return new DirectInserter();
    }

    protected boolean cancelClick() {
        return true;
    }

    protected int getPage() {
        return this.page;
    }

    protected void setPage(int page) {
        this.page = page;
    }

    protected Player getPlayer() {
        return player;
    }

    protected AdvancedInventoryManager getManager() {
        return this.manager;
    }

    protected int getUsableSize() {
        return this.getSize() - (this.selectorsEnabled ? 9 : 0);
    }

    protected AdvancedInventory getPrevious() {
        return previous;
    }

    public abstract int getSize();

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
        boolean needsOpen = this.needsOpen();
        this.inserter.stopInserting();
        this.items.clear();
        if (this.hasSelectors())
            this.setSelector();

        this.inserter = !this.open ? this.getInserter() : new DirectInserter();
        this.refreshContent();

        int size = this.getSize();
        this.inserter.setItems(manager.getOwnerInstance(), this, this.getContent(size), player, size);

        if (!needsOpen)
            this.player.updateInventory();
        else
            this.player.openInventory(this.inventory);

        this.open = true;
    }
}