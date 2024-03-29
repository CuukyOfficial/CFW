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

package de.varoplugin.cfw.player.hook;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Objects;

public abstract class AbstractHookEvent<T extends PlayerHook, E extends Event & Cancellable> extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final T hook;
    private final E source;

    public AbstractHookEvent(T hook, E source) {
        this.hook = hook;
        this.source = source;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.source.setCancelled(cancel);
    }

    @Override
    public boolean isCancelled() {
        return this.source.isCancelled();
    }

    public T getHook() {
        return this.hook;
    }

    public E getSource() {
        return this.source;
    }

    @Override
    public HandlerList getHandlers() {
        return Objects.requireNonNull(handlers);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
