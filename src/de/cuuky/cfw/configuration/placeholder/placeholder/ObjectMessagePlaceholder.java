package de.cuuky.cfw.configuration.placeholder.placeholder;

import de.cuuky.cfw.configuration.placeholder.MessagePlaceholder;
import de.cuuky.cfw.configuration.placeholder.placeholder.type.MessagePlaceholderType;

import java.util.HashMap;
import java.util.Map;

public abstract class ObjectMessagePlaceholder<T> extends MessagePlaceholder {

    private final Map<T, String> placeholderValues = new HashMap<>();
    private final Map<T, Long> placeholderRefreshes = new HashMap<>();

    public ObjectMessagePlaceholder(String identifier, int refreshDelay, String description) {
        super(MessagePlaceholderType.OBJECT, identifier, refreshDelay, description);
    }

    private void checkRefresh(T player) {
        if (!this.shallRefresh(player)) return;
        this.refreshValue(player);
    }

    private boolean shallRefresh(T player) {
        if (!this.placeholderRefreshes.containsKey(player)) return true;
        return this.shallRefresh(this.placeholderRefreshes.get(player));
    }

    private void refreshValue(T player) {
        this.placeholderValues.put(player, this.getValue(player));
        this.placeholderRefreshes.put(player, System.currentTimeMillis());
    }

    protected abstract String getValue(T object);

    @Override
    public String replacePlaceholder(String message, Object... objects) {
        T object = (T) objects[0];
        this.checkRefresh(object);
        String value = placeholderValues.get(object);
        return message.replace(this.getIdentifier(), value != null ? value : "");
    }

    @Override
    public void clearValue() {
        this.placeholderValues.clear();
        this.placeholderRefreshes.clear();
    }
}