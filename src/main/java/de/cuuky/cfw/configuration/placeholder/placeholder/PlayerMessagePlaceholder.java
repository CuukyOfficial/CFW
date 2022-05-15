package de.cuuky.cfw.configuration.placeholder.placeholder;

import de.cuuky.cfw.player.CustomPlayer;

public abstract class PlayerMessagePlaceholder extends ObjectMessagePlaceholder<CustomPlayer> {

	public PlayerMessagePlaceholder(String identifier, int refreshDelay, String description) {
		super(identifier, refreshDelay, description);
	}
}