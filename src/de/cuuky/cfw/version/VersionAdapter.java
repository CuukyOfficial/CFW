package de.cuuky.cfw.version;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

public interface VersionAdapter {

	Object getConnection(Player player);

	int getPing(Player player);

	void respawnPlayer(Player player);

	void sendActionbar(Player player, String message, int duration, Plugin instance);

	void sendActionbar(Player player, String message);

	void sendLinkedMessage(Player player, String message, String link);

	void sendPacket(Player player, Object packet);

	void sendTablist(Player player, String header, String footer);

	void sendTitle(Player player, String header, String footer);

	void setAttributeSpeed(Player player, double value);
	
	void setNametagVisibility(Team team, boolean shown);
	
	void setArmorstandAttributes(Entity armorstand, boolean visible, boolean customNameVisible, boolean gravity, String customName);
	
	void setXpCooldown(Player player, int cooldown);
	
	void deleteItemAnnotations(ItemStack item);

	Object getNetworkManager(Player player);

	String getLocale(Player player);
}