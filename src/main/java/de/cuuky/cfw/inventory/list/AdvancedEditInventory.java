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

package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.inventory.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public abstract class AdvancedEditInventory extends AdvancedItemShowInventory implements EventNotifiable {

    private Map<Integer, ItemStack> contents;
//    private final Set<Integer> updated = new HashSet<>();
    private int size = -1;

    public AdvancedEditInventory(AdvancedInventoryManager manager, Player player) {
        super(manager, player);

//        this.size = this.getRecommendedSize();
        this.addInfo(new PrioritisedInfo(this::getEditPriority, Info.PLAY_SOUND));
        this.addInfo(new PrioritisedInfo(this::getEditPriority, Info.CANCEL_CLICK));
    }

    private void loadItems() {
        this.contents = new TreeMap<>();
        Collection<ItemStack> items = this.getInitialItems();
        if (items == null)
            return;

        int i = 0;
        for (ItemStack stack : items) {
            this.contents.put(i, stack);
            i++;
        }
    }

//    private void updatePage() {
//        this.updated.add(this.getPage());
//    }

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
        ItemStack stack = this.contents.get(index);
        return stack == null ? null : new AbstractMap.SimpleEntry<>(stack, null);
    }

    @Override
    public final int getSize() {
        return this.size == -1 ? this.size = this.getRecommendedSize() : this.size;
    }

    @Override
    public Consumer<Player> getSoundPlayer() {
        return this.clickedContent() ? null : super.getSoundPlayer();
    }

    @Override
    public boolean cancelClick() {
        return !this.clickedContent() && super.cancelClick();
    }

    protected abstract Collection<ItemStack> getInitialItems();

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
        if (this.contents == null) this.loadItems();
        super.update();
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