package de.cuuky.cfw.player;

import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;

public class LanguagePlayerManager extends FrameworkManager {
	
	private ArrayList<CustomLanguagePlayer> players;

	public LanguagePlayerManager(JavaPlugin ownerInstance) {
		super(FrameworkManagerType.PLAYER, ownerInstance);
		
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
	
	public ArrayList<CustomLanguagePlayer> getPlayers() {
		return this.players;
	}
}