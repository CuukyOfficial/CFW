package de.cuuky.cfw.player;

import org.bukkit.entity.Player;

import de.cuuky.cfw.player.clientadapter.BoardUpdateHandler;

public interface CustomPlayer {

	public String getUUID();

	public String getName();

	public Player getPlayer();

	public String getLocale();

	public BoardUpdateHandler<? extends CustomPlayer> getUpdateHandler();

}