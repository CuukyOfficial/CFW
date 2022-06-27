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

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

public abstract class AbstractHookBuilder<T extends PlayerHook> implements HookBuilder<T> {

    protected boolean cancelEvent = true;
    protected final Collection<AbstractPlayerHook.HookSubscriber<? extends AbstractHookEvent<?, ?>>> subscribers;

    protected AbstractHookBuilder() {
        this.subscribers = new LinkedList<>();
    }

    @Override
    public <E extends AbstractHookEvent<?, ?>> HookBuilder<T> subscribe(Class<E> eventType, Consumer<E> eventConsumer) {
        this.subscribers.add(new AbstractPlayerHook.HookSubscriber<>(eventType, eventConsumer));
        return this;
    }

    @Override
    public HookBuilder<T> cancel(boolean cancel) {
        this.cancelEvent = cancel;
        return this;
    }

    @Override
    public T complete(Player player, Plugin plugin) {
        T built = this.build();
        built.register(player, plugin);
        return built;
    }
}
