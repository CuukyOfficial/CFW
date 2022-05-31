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

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.FillerConfigurable;
import de.cuuky.cfw.inventory.Info;
import de.cuuky.cfw.inventory.ItemClick;

public abstract class AdvancedItemShowInventory extends AdvancedInventory implements FillerConfigurable {

    public AdvancedItemShowInventory(AdvancedInventoryManager manager, Player player) {
        super(manager, player);
    }

    protected void addShowItem(int i, ItemStack stack, ItemClick click) {
        this.addItem(i, stack, click);
    }

    protected int getIndex(int page) {
        return this.getUsableSize() * (page - 1);
    }

    protected int getCurrentIndex() {
        return this.getIndex(this.getPage());
    }

    protected abstract Map.Entry<ItemStack, ItemClick> getInfo(int index);

    protected int getRecommendedSize() {
        return Math.min(this.toInvSize(this.getMinSize()) + this.getInfo(Info.HOTBAR_SIZE), 54);
    }

    protected int getMinSize() {
        return 27;
    }

    @Override
    public int getSize() {
        return this.getRecommendedSize();
    }

    @Override
    protected final int getStartPage() {
        return 1;
    }

    @Override
    protected final int getMinPage() {
        return 1;
    }

    @Override
    public abstract int getMaxPage();

    @Override
    public boolean shallInsertFiller(int location, ItemStack stack) {
        return location >= this.getUsableSize();
    }

    @Override
    public void refreshContent() {
        int start = this.getCurrentIndex(), usable = this.getUsableSize();
        for (int i = 0; i < usable; i++) {
            Map.Entry<ItemStack, ItemClick> info = this.getInfo(i + start);
            if (info == null)
                continue;
            this.addShowItem(i, info.getKey(), info.getValue());
        }
    }
}
