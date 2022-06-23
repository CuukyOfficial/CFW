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

import de.varoplugin.cfw.player.hook.AbstractHookBuilder;
import de.varoplugin.cfw.player.hook.HookEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class PlayerItemHookBuilder extends AbstractHookBuilder<ItemHook> implements ItemHookBuilder {

    protected ItemStack item;
    protected int slot = 0;
    protected boolean movable = true;
    protected boolean droppable = false;

    @Override
    public ItemHook build() {
        return new PlayerItemHook(this.cancelEvent, this.subscribers, this.item, this.slot, this.movable, this.droppable);
    }

    @Override
    public <E extends HookEvent<?, ?>> ItemHookBuilder subscribe(Class<E> eventType, Consumer<E> eventConsumer) {
        return (ItemHookBuilder) super.subscribe(eventType, eventConsumer);
    }

    @Override
    public ItemHookBuilder item(ItemStack item) {
        this.item = item;
        return this;
    }

    @Override
    public ItemHookBuilder slot(int slot) {
        this.slot = slot;
        return this;
    }

    @Override
    public ItemHookBuilder movable(boolean movable) {
        this.movable = movable;
        return this;
    }

    @Override
    public ItemHookBuilder droppable(boolean droppable) {
        this.droppable = droppable;
        return this;
    }
}