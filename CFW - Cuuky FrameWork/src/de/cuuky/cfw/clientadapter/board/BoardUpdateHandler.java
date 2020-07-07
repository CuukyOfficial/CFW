package de.cuuky.cfw.clientadapter.board;

import java.util.ArrayList;

import de.cuuky.cfw.player.CustomPlayer;

public interface BoardUpdateHandler<T extends CustomPlayer> {

	public ArrayList<String> getTablistHeader(T player);

	public ArrayList<String> getTablistFooter(T player);

	public String getTablistName(T player);

	public String getScoreboardTitle(T player);

	public ArrayList<String> getScoreboardEntries(T player);

	public String getNametagName(T player);

	public String getNametagPrefix(T player);

	public String getNametagSuffix(T player);

	public boolean isNametagVisible(T player);

}