package de.cuuky.cfw.configuration.placeholder.placeholder;

import java.util.HashMap;

import de.cuuky.cfw.configuration.placeholder.MessagePlaceholder;
import de.cuuky.cfw.configuration.placeholder.placeholder.type.MessagePlaceholderType;
import de.cuuky.cfw.player.CustomPlayer;

public abstract class PlayerMessagePlaceholder extends MessagePlaceholder {

	private HashMap<CustomPlayer, String> placeholderValues;
	private HashMap<CustomPlayer, Long> placeholderRefreshes;

	public PlayerMessagePlaceholder(String identifier, int refreshDelay, String description) {
		super(MessagePlaceholderType.PLAYER, identifier, refreshDelay, description);

		this.placeholderValues = new HashMap<>();
		this.placeholderRefreshes = new HashMap<>();
	}

	private void checkRefresh(CustomPlayer player) {
		if (!shallRefresh(player))
			return;

		refreshValue(player);
	}

	private boolean shallRefresh(CustomPlayer player) {
		if (!this.placeholderRefreshes.containsKey(player))
			return true;

		return this.shallRefresh(this.placeholderRefreshes.get(player));
	}

	private void refreshValue(CustomPlayer player) {
		this.placeholderValues.put(player, getValue(player));
		this.placeholderRefreshes.put(player, System.currentTimeMillis());
	}

	protected abstract String getValue(CustomPlayer player);

	public String replacePlaceholder(String message, CustomPlayer player) {
		checkRefresh(player);
		
		String value = placeholderValues.get(player);
		return message.replace(identifier, value != null ? value : "");
	}

	@Override
	public void clearValue() {
		placeholderValues.clear();
		placeholderRefreshes.clear();
	}
}