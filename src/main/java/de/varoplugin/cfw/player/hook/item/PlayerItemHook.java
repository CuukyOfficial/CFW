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

package de.varoplugin.cfw.player.hook.item;

import de.varoplugin.cfw.player.hook.AbstractPlayerHook;
import de.varoplugin.cfw.player.hook.AbstractHookEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

public class PlayerItemHook extends AbstractPlayerHook<PlayerItemHookListener> implements ItemHook {

    private final ItemStack item;
    private final int slot;
    private final boolean movable;
    private final boolean droppable;

    public PlayerItemHook(boolean cancel, Collection<HookSubscriber<? extends AbstractHookEvent<?, ?>>> subscriber, ItemStack item, int slot, boolean movable, boolean droppable) {
        super(cancel, subscriber, new PlayerItemHookListener());

        if (item == null)
            throw new IllegalArgumentException("Missing item");

        this.item = item;
        this.slot = slot;
        this.movable = movable;
        this.droppable = droppable;
    }

    @Override
    protected void registerListener(PlayerItemHookListener listener) {
        listener.register(this);
    }

    @Override
    protected void onRegister(Player player, Plugin plugin) {
        player.getInventory().setItem(this.slot, this.item);
        player.updateInventory();
        super.onRegister(player, plugin);
    }

    @Override
    protected void onUnregister() {
        this.getPlayer().getInventory().remove(this.item);
        super.onUnregister();
    }

    @Override
    public ItemStack getItem() {
        return this.item;
    }

    @Override
    public int getSlot() {
        return this.slot;
    }

    @Override
    public boolean isMovable() {
        return this.movable;
    }

    @Override
    public boolean isDroppable() {
        return this.droppable;
    }
}
