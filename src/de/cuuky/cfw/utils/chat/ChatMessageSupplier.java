package de.cuuky.cfw.utils.chat;

public interface ChatMessageSupplier<T> {

    String getTitle(PageableChat<T> chat);

    String getFooter(PageableChat<T> chat);

    String getInvalidPage(int wrongPage);

    String getEntry(T item);

    String getNoEntriesFound();
}