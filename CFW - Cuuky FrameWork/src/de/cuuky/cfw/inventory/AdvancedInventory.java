package de.cuuky.cfw.inventory;

import de.cuuky.cfw.inventory.inserter.DirectInserter;
import de.cuuky.cfw.version.types.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AdvancedInventory implements ItemOverrideable {

    private final Map<Integer, Supplier<AdvancedItemLink>> selectors = new HashMap<Integer, Supplier<AdvancedItemLink>>() {{
        put(0, AdvancedInventory.this::getBackwardsItem);
        put(3, AdvancedInventory.this::getBackItem);
        put(4, AdvancedInventory.this::getCloseItem);
        put(8, AdvancedInventory.this::getForwardItem);
    }};

    private AdvancedInventory previous;

    private final AdvancedInventoryManager manager;
    private final Player player;
    private final int size;
    private int page = 1;
    private Inventory inventory;
    private ItemInserter inserter;
    private boolean selectorsEnabled, open;

    private final Map<Integer, AdvancedItemLink> items = new HashMap<>();

    public AdvancedInventory(AdvancedInventoryManager manager, int size, Player player) {
        this.manager = manager;
        this.size = size;
        this.player = player;
        this.inserter = this.getInserter();

        this.open();
    }

    private void createInventory() {
        this.inventory = this.manager.getOwnerInstance().getServer().createInventory(this.player, this.size, this.getTitle());
    }

    private int convertPage(int toGo) {
        int max = this.getMaxPage();
        return Math.min(toGo, max);
    }

    private AdvancedItemLink getCheckedLink(Supplier<ItemStack> sup, ItemClick click) {
        ItemStack stack = sup.get();
        return stack == null ? null : new AdvancedItemLink(stack, click);
    }

    private AdvancedItemLink generateNavigator(Supplier<ItemStack> sup, int noAdd, int add) {
        if (noAdd == this.page)
            return null;

        return this.getCheckedLink(sup, event -> this.page = this.convertPage(this.page + add));
    }

    private Map<Integer, ItemStack> getContent(int size) {
        Map<Integer, ItemStack> stacks = new HashMap<>();
        ItemStack filler = this.getFillerStack(this);
        for (int i = 0; i < size; i++) {
            AdvancedItemLink link = this.items.get(i);
            if (link == null) {
                if (filler != null)
                    stacks.put(i, filler);
            } else
                stacks.put(i, link.getStack());
        }

        return stacks;
    }

    private void setSelector() {
        for (Integer index : this.selectors.keySet()) {
            AdvancedItemLink link = this.selectors.get(index).get();
            if (link == null)
                continue;

            int realIndex = this.getUsableSize() + index;
            this.items.put(realIndex, link);
            this.inventory.setItem(realIndex, link.getStack());
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

    protected abstract int getMaxPage();

    protected abstract void refreshContent();

    protected void playSound() {
        this.player.playSound(player.getLocation(), Sounds.CLICK.bukkitSound(), 1, 1);
    }

    protected boolean hasSelectors() {
        return (selectorsEnabled = this.selectors.keySet().stream().anyMatch(i -> this.selectors.get(i).get() != null));
    }

    protected final AdvancedItemLink getBackwardsItem() {
        return this.generateNavigator(() -> this.getBackwardsStack(this), 1, -1);
    }

    protected final AdvancedItemLink getForwardItem() {
        return this.generateNavigator(() -> this.getForwardStack(this), this.getMaxPage(), 1);
    }

    protected final AdvancedItemLink getCloseItem() {
        return this.getCheckedLink(() -> this.getCloseStack(this), event -> this.close());
    }

    protected final AdvancedItemLink getBackItem() {
        return this.previous == null ? null : this.getCheckedLink(() -> this.getBackStack(this), event -> {
            this.close(false);
            previous.open();
        });
    }

    protected void addItem(int index, ItemStack stack, ItemClick click) {
        this.items.put(index, new AdvancedItemLink(stack, click));
        if (this.inserter.hasStarted())
            this.addToInventory(index, stack, true);
    }

    protected void addItem(int index, ItemStack stack) {
        this.addItem(index, stack, null);
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
        return this.size - (this.selectorsEnabled ? 9 : 0);
    }

    protected AdvancedInventory getPrevious() {
        return previous;
    }

    public void open() {
        manager.registerInventory(this);
        new BukkitRunnable() {

            @Override
            public void run() {
                update();
            }
        }.runTaskLater(manager.getOwnerInstance(), 0L);
    }

    public void close(boolean close) {
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

        this.inserter = this.getInserter();
        this.refreshContent();
        int size = this.getUsableSize();
        this.inserter.setItems(manager.getOwnerInstance(), this, this.getContent(size), player, size);

        if (!needsOpen)
            this.player.updateInventory();
        else
            this.player.openInventory(this.inventory);

        this.open = true;
    }

    public int getSize() {
        return size;
    }
}