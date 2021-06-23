package de.cuuky.cfw.player.connection;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.cuuky.cfw.version.VersionAdapter;
import de.cuuky.cfw.version.VersionUtils;

@Deprecated
public class NetworkManager {

	private Player player;
	private VersionAdapter internal;

	public NetworkManager(Player player) {
		this.player = player;
		this.internal = VersionUtils.getVersion().getAdapter();
	}
	
	public Object getConnection() {
		return this.internal.getConnection(this.player);
	}
	
	public int getPing() {
		return this.internal.getPing(this.player);
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public void respawnPlayer() {
		this.internal.respawnPlayer(this.player);
	}
	
	public void sendActionbar(String message, int duration, Plugin instance) {
		this.internal.sendActionbar(this.player, message, duration, instance);
	}
	
	public void sendActionbar(String message) {
		this.internal.sendActionbar(this.player, message);
	}
	
	public void sendLinkedMessage(String message, String link) {
		this.internal.sendLinkedMessage(this.player, message, link);
	}
	
	public void sendPacket(Object packet) {
		this.internal.sendPacket(this.player, packet);
	}
	
	public void sendTablist(String header, String footer) {
		this.internal.sendTablist(this.player, header, footer);
	}
	
	public void sendTitle(String header, String footer) {
		this.internal.sendTitle(this.player, header, footer);
	}
	
	public void setAttributeSpeed(double value) {
		this.internal.setAttributeSpeed(this.player, value);
	}
	
	public Object getNetworkManager() {
		return this.internal.getNetworkManager(this.player);
	}
	
	public String getLocale() {
		return this.internal.getLocale(this.player);
	}
}