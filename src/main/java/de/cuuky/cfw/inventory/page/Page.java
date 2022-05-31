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

package de.cuuky.cfw.inventory.page;

import org.bukkit.inventory.ItemStack;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.ContentRefreshable;
import de.cuuky.cfw.inventory.InfoProvider;
import de.cuuky.cfw.inventory.ItemClick;

public interface Page<Inventory extends AdvancedInventory> extends InfoProvider, ContentRefreshable {

    Inventory getInventory();

    @Override
    default int getPriority() {
        return 1;
    }

    default void addItem(int index, ItemStack stack, ItemClick click) {
        this.getInventory().addItem(index, stack, click);
    }

    default void addItem(int index, ItemStack stack) {
        this.getInventory().addItem(index, stack, null);
    }
}
