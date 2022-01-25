package de.cuuky.cfw.version;

import java.util.Collection;
import java.util.Properties;

import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

public interface VersionAdapter {

	Collection<? extends Player> getOnlinePlayers();

	Object getConnection(Player player);

	int getPing(Player player);

	void respawnPlayer(Player player);

	void sendActionbar(Player player, String message, int duration, Plugin instance);

	void sendActionbar(Player player, String message);

    void sendClickableMessage(Player player, String message, ClickEvent.Action action, String value);

	void sendLinkedMessage(Player player, String message, String link);

	void sendTablist(Player player, String header, String footer);

	void sendTitle(Player player, String title, String subtitle);

	void setAttributeSpeed(Player player, double value);

	void setNametagVisibility(Team team, boolean shown);

	void setArmorstandAttributes(Entity armorstand, boolean visible, boolean customNameVisible, boolean gravity,
			String customName);

	void removeAi(LivingEntity entity);

	void setXpCooldown(Player player, int cooldown);

	void deleteItemAnnotations(ItemStack item);
	
	BlockFace getSignAttachedFace(Block block);

	Object getNetworkManager(Player player);

	String getLocale(Player player);

	void forceClearWorlds();

	Properties getServerProperties();

	boolean supportsAntiXray();

	void setAntiXrayEnabled(boolean enabled);
}
