package de.cuuky.cfw.player.clientadapter;

import java.util.ArrayList;

import de.cuuky.cfw.player.CustomPlayer;

public class BoardUpdateHandler<T extends CustomPlayer> {

	protected T player;

	public BoardUpdateHandler(T player) {
		this.player = player;
	}

	public ArrayList<String> getTablistHeader() {
		return null;
	}

	public ArrayList<String> getTablistFooter() {
		return null;
	}

	public String getTablistName() {
		return null;
	}

	public String getScoreboardTitle() {
		return null;
	}

	public ArrayList<String> getScoreboardEntries() {
		return null;
	}

	public String getNametagName() {
		return null;
	}

	public String getNametagPrefix() {
		return null;
	}

	public String getNametagSuffix() {
		return null;
	}

	public boolean isNametagVisible() {
		return true;
	}
}