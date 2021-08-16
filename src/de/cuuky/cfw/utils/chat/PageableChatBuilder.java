package de.cuuky.cfw.utils.chat;

import java.util.List;
import java.util.function.Supplier;

public class PageableChatBuilder<T> {

    private Supplier<List<T>> listSupplier;
    private ChatMessageSupplier<T> chatMessageSupplier;
    private int page = 1, entriesPerPage = 10;

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