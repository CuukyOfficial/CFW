package de.cuuky.cfw.version;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Properties;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

import net.md_5.bungee.api.chat.ClickEvent;

public class SpigotVersionAdapter implements VersionAdapter {

	private VersionAdapter parent;
	private Object spigot;

	public SpigotVersionAdapter(Supplier<VersionAdapter> parentSupplier) {
		this.parent = parentSupplier.get();
		try {
			this.spigot = Bukkit.getServer().getClass().getDeclaredMethod("spigot").invoke(Bukkit.getServer());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<? extends Player> getOnlinePlayers() {
		return this.parent.getOnlinePlayers();
	}

	@Override
	public Object getConnection(Player player) {
		return this.parent.getConnection(player);
	}

	@Override
	public int getPing(Player player) {
		return this.parent.getPing(player);
	}

	@Override
	public void respawnPlayer(Player player) {
		this.parent.respawnPlayer(player);
	}

	@Override
	public void sendActionbar(Player player, String message, int duration, Plugin instance) {
		this.parent.sendActionbar(player, message, duration, instance);
	}

	@Override
	public void sendActionbar(Player player, String message) {
		this.parent.sendActionbar(player, message);
	}

	@Override
	public void sendClickableMessage(Player player, String message, ClickEvent.Action action, String value) {
		this.parent.sendClickableMessage(player, message, action, value);
	}

	@Override
	public void sendLinkedMessage(Player player, String message, String link) {
		this.parent.sendLinkedMessage(player, message, link);
	}

	@Override
	public void sendTablist(Player player, String header, String footer) {
		this.parent.sendTablist(player, header, footer);
	}

	@Override
	public void sendTitle(Player player, String title, String subtitle) {
		this.parent.sendTitle(player, title, subtitle);
	}

	@Override
	public void setAttributeSpeed(Player player, double value) {
		this.parent.setAttributeSpeed(player, value);
	}

	@Override
	public void setNametagVisibility(Team team, boolean shown) {
		this.parent.setNametagVisibility(team, shown);
	}

	@Override
	public void setArmorstandAttributes(Entity armorstand, boolean visible, boolean customNameVisible, boolean gravity,
			String customName) {
		this.parent.setArmorstandAttributes(armorstand, visible, customNameVisible, gravity, customName);
	}

	@Override
	public void removeAi(LivingEntity entity) {
		this.parent.removeAi(entity);
	}

	@Override
	public void setXpCooldown(Player player, int cooldown) {
		this.parent.setXpCooldown(player, cooldown);
	}

	@Override
	public void deleteItemAnnotations(ItemStack item) {
		this.parent.deleteItemAnnotations(item);
	}

	@Override
	public BlockFace getSignAttachedFace(Block block) {
		return this.parent.getSignAttachedFace(block);
	}

	@Override
	public Object getNetworkManager(Player player) {
		return this.parent.getNetworkManager(player);
	}

	@Override
	public String getLocale(Player player) {
		return this.parent.getLocale(player);
	}

	@Override
	public void forceClearWorlds() {
		this.parent.forceClearWorlds();
	}

	@Override
	public Properties getServerProperties() {
		return this.parent.getServerProperties();
	}

	@Override
	public void setServerProperty(String key, Object value) {
		this.parent.setServerProperty(key, value);
	}

	@Override
	public boolean supportsAntiXray() {
		return false;
	}

	@Override
	public void setAntiXrayEnabled(boolean enabled) {
		// TODO Auto-generated method stub
	}
}
