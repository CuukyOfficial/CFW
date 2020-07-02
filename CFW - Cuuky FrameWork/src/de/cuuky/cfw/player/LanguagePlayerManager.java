package de.cuuky.cfw.player;

import java.util.ArrayList;

import de.cuuky.cfw.CuukyFrameWork;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;

public class LanguagePlayerManager extends FrameworkManager {
	
	private ArrayList<CustomLanguagePlayer> players;

	public LanguagePlayerManager(CuukyFrameWork framework) {
		super(FrameworkManagerType.PLAYER, framework);
		
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