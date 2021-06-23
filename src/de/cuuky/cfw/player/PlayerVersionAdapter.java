package de.cuuky.cfw.player;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.cuuky.cfw.version.VersionAdapter;
import de.cuuky.cfw.version.VersionUtils;

public class PlayerVersionAdapter {

	private Player player;
	private VersionAdapter internal;

	public PlayerVersionAdapter(Player player) {
		this.player = player;
		this.internal = VersionUtils.getVersionAdapter();
	}
	
	public int getPing() {
		return this.internal.getPing(this.player);
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
	
	public String getLocale() {
		return this.internal.getLocale(this.player);
	}
}
