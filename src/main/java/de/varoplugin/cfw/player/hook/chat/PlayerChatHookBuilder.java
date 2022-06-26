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

package de.varoplugin.cfw.player.hook.chat;

import de.varoplugin.cfw.player.hook.AbstractHookBuilder;
import de.varoplugin.cfw.player.hook.AbstractHookEvent;

import java.util.function.Consumer;

public class PlayerChatHookBuilder extends AbstractHookBuilder<ChatHook> implements ChatHookBuilder {

    protected String message;

    @Override
    public ChatHookBuilder message(String message) {
        this.message = message;
        return this;
    }

    @Override
    public <E extends AbstractHookEvent<?, ?>> ChatHookBuilder subscribe(Class<E> eventType, Consumer<E> eventConsumer) {
        return (ChatHookBuilder) super.subscribe(eventType, eventConsumer);
    }

    @Override
    public ChatHook build() {
        return new PlayerChatHook(this.cancelEvent, this.subscribers, this.message);
    }
}