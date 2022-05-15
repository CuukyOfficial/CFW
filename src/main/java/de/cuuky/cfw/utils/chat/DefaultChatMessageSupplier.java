package de.cuuky.cfw.utils.chat;

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