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

package de.cuuky.cfw.hooking.hooks.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.hooking.hooks.HookEntity;
import de.cuuky.cfw.hooking.hooks.HookEntityType;

public class ItemHook extends HookEntity {

    private final ItemHookHandler hookListener;
    private final int slot;
    private final ItemStack stack;
    private boolean dropable = false, dragable = false;

    public ItemHook(Player player, ItemStack stack, int slot, ItemHookHandler listener) {
        super(HookEntityType.ITEM, player);

        this.hookListener = listener;
        this.slot = slot;
        this.stack = stack;

        player.getInventory().setItem(slot, stack);
        player.updateInventory();
    }

    @Override
    protected HookEntity getExisting(HookManager manager, Player player) {
        return manager.getItemHook(stack, player);
    }

    public void setDropable(boolean dropable) {
        this.dropable = dropable;
    }

    public boolean isDropable() {
        return dropable;
    }

    public void setDragable(boolean dragable) {
        this.dragable = dragable;
    }

    public boolean isDragable() {
        return dragable;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public ItemHookHandler getHookListener() {
        return hookListener;
    }

    public int getSlot() {
        return slot;
    }
}
