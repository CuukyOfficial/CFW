package de.cuuky.cfw.configuration.placeholder.placeholder;

import java.util.ArrayList;
import java.util.HashMap;

import de.cuuky.cfw.configuration.placeholder.MessagePlaceholder;
import de.cuuky.cfw.player.CustomPlayer;

public abstract class PlayerMessagePlaceholder extends MessagePlaceholder {

	private static ArrayList<PlayerMessagePlaceholder> playerPlaceholder;
	private static HashMap<String, ArrayList<PlayerMessagePlaceholder>> cachedRequests;

	static {
		cachedRequests = new HashMap<>();
	}

	private HashMap<CustomPlayer, String> placeholderValues;
	private HashMap<CustomPlayer, Long> placeholderRefreshes;

	public PlayerMessagePlaceholder(String identifier, int refreshDelay, String description) {
		super(identifier, refreshDelay, description);

		this.placeholderValues = new HashMap<>();
		this.placeholderRefreshes = new HashMap<>();

		if(playerPlaceholder == null)
			playerPlaceholder = new ArrayList<>();

		playerPlaceholder.add(this);
	}

	private void checkRefresh(CustomPlayer player) {
		if(!shallRefresh(player))
			return;

		refreshValue(player);
	}

	private boolean shallRefresh(CustomPlayer player) {
		if(!this.placeholderRefreshes.containsKey(player))
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

		return message.replace(identifier, placeholderValues.get(player));
	}

	@Override
	public void clearValue() {
		placeholderValues.clear();
		placeholderRefreshes.clear();
	}

	private static Object[] replaceByList(String value, CustomPlayer vp, ArrayList<PlayerMessagePlaceholder> list) {
		ArrayList<PlayerMessagePlaceholder> cached = new ArrayList<>();
		for(PlayerMessagePlaceholder pmp : list)
			if(pmp.containsPlaceholder(value)) {
				value = pmp.replacePlaceholder(value, vp);
				cached.add(pmp);
			}

		return new Object[] { value, cached };
	}

	@SuppressWarnings("unchecked")
	public static String replacePlaceholders(String value, CustomPlayer vp) {
		if(cachedRequests.get(value) != null)
			return (String) replaceByList(value, vp, cachedRequests.get(value))[0];
		else {
			Object[] result = replaceByList(value, vp, playerPlaceholder);
			cachedRequests.put(value, (ArrayList<PlayerMessagePlaceholder>) result[1]);
			return (String) result[0];
		}
	}
	
	public static void clearCache() {
		cachedRequests.clear();
	}

	public static ArrayList<PlayerMessagePlaceholder> getPlayerPlaceholder() {
		return playerPlaceholder;
	}
}