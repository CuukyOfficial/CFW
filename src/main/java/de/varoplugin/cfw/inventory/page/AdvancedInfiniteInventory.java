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

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.varoplugin.cfw.inventory.AdvancedInventory;
import de.varoplugin.cfw.inventory.AdvancedInventoryManager;
import de.varoplugin.cfw.inventory.ItemClick;

public abstract class AdvancedInfiniteInventory extends AdvancedInventory {

    private int max = 1;

    public AdvancedInfiniteInventory(AdvancedInventoryManager manager, Player player) {
        super(manager, player);
    }

    private void checkMax(int test) {
        if (test > this.max)
            this.max = test;
    }

    protected int getIndex() {
        return (this.getPage() - 1) * this.getUsableSize();
    }

    private int convertIndex(int index) {
        return index - this.getIndex();
    }

    @Override
    protected final int getMaxPage() {
        return (int) Math.ceil((double) this.max / (double) this.getUsableSize());
    }

    @Override
    public final void addItem(int index, ItemStack stack) {
        this.addItem(index, stack, null);
    }

    @Override
    public final void addItem(int index, ItemStack stack, ItemClick click) {
        this.checkMax(index);
        index = this.convertIndex(index);
        if (0 <= index && index < this.getUsableSize())
            super.addItem(index, stack, click);
    }
}
