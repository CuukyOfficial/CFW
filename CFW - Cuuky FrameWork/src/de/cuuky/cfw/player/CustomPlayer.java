package de.cuuky.cfw.player;

import org.bukkit.entity.Player;

import de.cuuky.cfw.player.connection.NetworkManager;

public interface CustomPlayer {
	
	public NetworkManager getNetworkManager();
	public Player getPlayer();
	public String getName();
	public String getUUID();

}