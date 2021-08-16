package de.cuuky.cfw.utils.chat;

import org.bukkit.command.CommandSender;

import java.util.List;

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
        if (message != null) sender.sendMessage(message);
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
        if (this.invalid(sender)) return;
        this.sendMessage(sender, this.title);
        int start = (this.page - 1) * this.entriesPerPage, max = Math.min(start + this.entriesPerPage, list.size());
        for (int i = start; i < max; i++) this.sendMessage(sender, this.messages.getEntry(list.get(i)));
        this.sendMessage(sender, this.footer);
    }

    public int getMaxPage() {
        return this.maxPage;
    }

    public int getPage() {
        return page;
    }

    public int getEntriesPerPage() {
        return entriesPerPage;
    }
}