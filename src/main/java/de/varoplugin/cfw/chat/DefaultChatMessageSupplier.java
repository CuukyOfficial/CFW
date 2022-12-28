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

package de.varoplugin.cfw.chat;

import java.util.function.Function;

public class DefaultChatMessageSupplier<T> implements ChatMessageSupplier<T> {

    private final Function<T, String> entryFunction;

    public DefaultChatMessageSupplier(Function<T, String> entryFunction) {
        this.entryFunction = entryFunction;
    }

    @Override
    public String getTitle(PageableChat<T> chat) {
        return "§8--- §fPage §7(§f" + chat.getPage() + "§7/§f" + chat.getMaxPage() + "§7) §8---";
    }

    @Override
    public String getFooter(PageableChat<T> chat) {
        return null;
    }

    @Override
    public String getEntry(T item) {
        return this.entryFunction.apply(item);
    }

    @Override
    public String getNoEntriesFound() {
        return "No entries found!";
    }

    @Override
    public String getInvalidPage(int wrongPage) {
        return "Invalid page number!";
    }
}
