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

import java.util.List;

import org.bukkit.command.CommandSender;

public class PageableChat<T> {

    private final List<T> list;
    private final ChatMessageSupplier<T> messages;
    private final String title, footer, invalidPage, emptyList;
    private final int page, entriesPerPage, maxPage;

    PageableChat(List<T> list, ChatMessageSupplier<T> messages, int page, int entriesPerPage) {
        this.list = list;
        this.messages = messages;
        this.page = page;
        this.entriesPerPage = entriesPerPage;
        this.maxPage = 1 + (this.list.size() - 1) / this.entriesPerPage;
        this.title = this.messages.getTitle(this);
        this.footer = this.messages.getFooter(this);
        this.invalidPage = this.messages.getInvalidPage(this.page);
        this.emptyList = this.messages.getNoEntriesFound();
    }

    private void sendMessage(CommandSender sender, String message) {
        if (message != null)
            sender.sendMessage(message);
    }

    private boolean invalid(CommandSender sender) {
        if (this.list.isEmpty()) {
            this.sendMessage(sender, this.emptyList);
            return true;
        }

        if (this.page > this.maxPage || this.page <= 0) {
            this.sendMessage(sender, this.invalidPage);
            return true;
        }

        return false;
    }

    public void send(CommandSender sender) {
        if (this.invalid(sender))
            return;

        StringBuilder message = new StringBuilder();
        if (this.title != null)
            message.append(this.title).append("\n");
        int start = (this.page - 1) * this.entriesPerPage, max = Math.min(start + this.entriesPerPage, this.list.size());
        for (int i = start; i < max; i++)
            message.append(this.messages.getEntry(this.list.get(i))).append("\n");
        if (this.footer != null)
            message.append(this.footer);

        this.sendMessage(sender, message.toString());
    }

    public int getMaxPage() {
        return this.maxPage;
    }

    public int getPage() {
        return this.page;
    }

    public int getEntriesPerPage() {
        return this.entriesPerPage;
    }
}
