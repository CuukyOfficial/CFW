package de.cuuky.cfw.player;

import org.bukkit.entity.Player;

import de.cuuky.cfw.player.clientadapter.BoardUpdateHandler;
import de.cuuky.cfw.player.connection.NetworkManager;

public interface CustomPlayer {

	public String getUUID();

	public String getName();

	public Player getPlayer();

	@Deprecated
	public NetworkManager getNetworkManager();

	public String getLocale();

	public BoardUpdateHandler<? extends CustomPlayer> getUpdateHandler();

}