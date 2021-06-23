package de.cuuky.cfw.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;

public class LanguagePlayerManager extends FrameworkManager {

	private List<CustomLanguagePlayer> players;

	public LanguagePlayerManager(JavaPlugin instance) {
		super(FrameworkManagerType.PLAYER, instance);

		this.players = new ArrayList<>();
	}

	public CustomLanguagePlayer registerPlayer(CustomLanguagePlayer player) {
		players.add(player);
		return player;
	}

	public CustomLanguagePlayer unregisterPlayer(CustomLanguagePlayer player) {
		players.remove(player);
		return player;
	}

	public List<CustomLanguagePlayer> getPlayers() {
		return this.players;
	}
}