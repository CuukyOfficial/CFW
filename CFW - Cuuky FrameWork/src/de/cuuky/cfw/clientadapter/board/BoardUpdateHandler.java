package de.cuuky.cfw.clientadapter.board;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public interface BoardUpdateHandler {
	
	public ArrayList<String> getTablistHeader(Player player);
	
	public ArrayList<String> getTablistFooter(Player player);

	public String getScoreboardTitle(Player player);
	
	public ArrayList<String> getScoreboardEntries(Player player);
	
	public boolean showHeartsBelowName(Player player);

	public String getNametagName(Player player);
	
	public String getNametagPrefix(Player player);
	
	public String getNametagSuffix(Player player);
	
	public boolean isNametagVisible(Player player); 
	
}