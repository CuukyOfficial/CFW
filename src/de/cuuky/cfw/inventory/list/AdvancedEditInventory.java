package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.inventory.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public abstract class AdvancedEditInventory extends AdvancedItemShowInventory implements EventNotifiable {

    private final Map<Integer, ItemStack> contents = new TreeMap<>();
    private final Set<Integer> updated = new HashSet<>();

    public AdvancedEditInventory(AdvancedInventoryManager manager, Player player) {
        super(manager, player);

        this.addInfo(new PrioritisedInfo(this::getEditPriority, Info.PLAY_SOUND));
        this.addInfo(new PrioritisedInfo(this::getEditPriority, Info.CANCEL_CLICK));
    }

    private void updated() {
        this.updated.add(this.getPage());
    }

    private void updateCurrentContent() {
        ItemStack[] content = this.getInventory().getContents();
        for (int i = 0; i < this.getUsableSize(); i++) this.contents.put(this.getCurrentIndex() + i, content[i]);
    }

    protected int getEditPriority() {
        return this.clickedContent() ? this.getOverridePriority() : 0;
    }

    protected int getOverridePriority() {
        return 10;
    }

    @Override
    protected Map.Entry<ItemStack, ItemClick> getInfo(int index) {
        ItemStack stack;
        if (this.updated.contains(this.getPage())) stack = this.contents.get(index);
        else this.contents.put(index, stack = this.getStack(index));
        return stack == null ? null : new AbstractMap.SimpleEntry<>(stack, null);
    }

    @Override
    public Consumer<Player> getSoundPlayer() {
        return this.clickedContent() ? null : super.getSoundPlayer();
    }

    @Override
    public boolean cancelClick() {
        return !this.clickedContent() && super.cancelClick();
    }

    protected abstract ItemStack getStack(int index);

    public Map<Integer, ItemStack> getContents() {
        return contents;
    }

    public Collection<ItemStack> getItems() {
        this.updateCurrentContent();
        return this.contents.values();
    }

    public ItemStack[] collectItems() {
        return this.getItems().toArray(new ItemStack[0]);
    }

    @Override
    protected void setPage(int page) {
        this.updateCurrentContent();
        super.setPage(page);
    }

    @Override
    public void update() {
        super.update();
        this.updated();
    }

    @Override
    public void onInventoryDrag(InventoryDragEvent event) {
        event.getInventorySlots().stream().filter(i -> !this.clickedContent(i))
                .findFirst().ifPresent(integer -> event.getNewItems().remove(integer));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
    }
}