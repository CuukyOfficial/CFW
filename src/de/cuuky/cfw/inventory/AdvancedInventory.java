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

public abstract class AdvancedInventory implements InventoryInfoProvider {

    private final Map<Supplier<ItemInfo>, Supplier<ItemClick>> selectors = new HashMap<Supplier<ItemInfo>, Supplier<ItemClick>>() {{
        put(AdvancedInventory.this::getBackwardsInfo, () -> generateNavigator(AdvancedInventory.this::getMinPage, -1));
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
    private int page = this.getStartPage(), interval = -1;
    private Inventory inventory;
    private ItemInserter inserter;
    private boolean open;
    private BukkitTask autoTask;

    private final Map<Integer, AdvancedItemLink> items = new HashMap<>();

    public AdvancedInventory(AdvancedInventoryManager manager, Player player) {
        this.manager = manager;
        this.player = player;
        this.inserter = this.getInserter();

        this.open();
    }

    private void createInventory() {
        String title = this.getTitle();
        title = title.length() > 32 ? title.substring(0, 32) : title;
        this.inventory = this.manager.getOwnerInstance().getServer().createInventory(this.player, this.getSize(), title);
    }

    private int convertPage(int max, int add) {
        int go = this.page + add;
        return add > 0 ? Math.min(go, max) : Math.max(go, max);
    }

    private ItemClick generateNavigator(Supplier<Integer> maxSup, int add) {
        int max = maxSup.get();
        if ((add < 0 && this.page <= max) || (add > 0 && this.page >= max))
            return null;

        return event -> this.page = this.convertPage(max, add);
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
        if (this.getHotbarSize() == 0)
            return;

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
        AdvancedInventory ai = this.manager.getInventory(inv);
        if (ai == null || !(this.getTitle().equals(ai.getTitle()) && this.getSize() == inv.getSize())) {
            this.createInventory();
            return true;
        } else this.inventory = inv;

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

    protected int getMaxPage() {
        return 1;
    }

    protected int getMinPage() {
        return 1;
    }

    protected int getStartPage() {
        return 1;
    }

    protected boolean doAnimation() {
        return !this.open;
    }

    protected int getHotbarSize() {
        return 9;
    }

    protected void playSound() {
        this.player.playSound(player.getLocation(), Sounds.CLICK.bukkitSound(), 1, 1);
    }

    protected int calculateInvSize(int entries) {
        int mod = entries % 9;
        int result = (mod != 0 ? (9 - mod) : mod) + entries;
        return Math.min(result, 54);
    }

    protected void updateOthers(Function<AdvancedInventory, Boolean> filter) {
        this.manager.updateInventories(this, filter);
    }

    protected void back() {
        this.close(false);
        if (previous != null)
            previous.open();
    }

    protected String getPageViewer() {
        return "§7" + this.getPage() + "§8/§7" + this.getMaxPage();
    }

    protected ItemInfo getBackwardsInfo() {
        return new ItemInfo(this.getUsableSize(), new ItemBuilder().material(Material.ARROW).displayname("§cBackwards " + this.getPageViewer()).build());
    }

    protected ItemInfo getForwardsInfo() {
        return new ItemInfo(this.getUsableSize() + 8, new ItemBuilder().material(Material.ARROW).displayname("§aForwards " + this.getPageViewer()).build());
    }

    protected ItemInfo getCloseInfo() {
        return new ItemInfo(this.getUsableSize() + 4, new ItemBuilder().material(Materials.REDSTONE.parseMaterial()).displayname("§4Close").build());
    }

    protected ItemInfo getBackInfo() {
        return new ItemInfo(this.getUsableSize() + 3, new ItemBuilder().material(Material.STONE_BUTTON).displayname("§cBack").build());
    }

    protected ItemStack getFillerStack() {
        return new ItemBuilder().displayname("§c").itemstack(Materials.BLACK_STAINED_GLASS_PANE.parseItem()).build();
    }

    protected void setAutoRefresh(int interval) {
        this.interval = interval;
    }

    protected ItemInserter getInserter() {
        return new DirectInserter();
    }

    protected boolean cancelClick(int index) {
        return true;
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
        return this.getSize() - this.getHotbarSize();
    }

    protected AdvancedInventory getPrevious() {
        return previous;
    }

    protected boolean isOpen() {
        return open;
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
        boolean needsOpen = this.needsOpen();
        this.inserter.stopInserting();
        this.items.clear();
        this.setSelector();

        this.inserter = this.doAnimation() ? this.getInserter() : new DirectInserter();
        this.refreshContent();

        int size = this.getSize();
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