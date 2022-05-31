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

package de.cuuky.cfw.utils.chat;

import java.util.List;
import java.util.function.Supplier;

public class PageableChatBuilder<T> {

    private Supplier<List<T>> listSupplier;
    private ChatMessageSupplier<T> chatMessageSupplier;
    private int page = 1, entriesPerPage = 10;

    public PageableChatBuilder() {
    }

    public PageableChatBuilder(Supplier<List<T>> listSupplier) {
        this.listSupplier = listSupplier;
    }

    public PageableChatBuilder<T> page(int page) {
        this.page = page;
        return this;
    }

    public PageableChatBuilder<T> entriesPerPage(int entriesPerPage) {
        this.entriesPerPage = entriesPerPage;
        return this;
    }

    public PageableChatBuilder<T> page(String page) throws NumberFormatException {
        return this.page(Integer.parseInt(page));
    }

    public PageableChatBuilder<T> list(Supplier<List<T>> listSupplier) {
        this.listSupplier = listSupplier;
        return this;
    }

    public PageableChatBuilder<T> messages(ChatMessageSupplier<T> chatMessageSupplier) {
        this.chatMessageSupplier = chatMessageSupplier;
        return this;
    }

    public PageableChat<T> build() {
        return new PageableChat<>(this.listSupplier.get(), this.chatMessageSupplier, this.page, this.entriesPerPage);
    }
}
