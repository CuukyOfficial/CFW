package de.cuuky.cfw.version;

import java.util.Collection;
import java.util.Properties;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

public class UnsupportedVersionAdapter implements VersionAdapter {

	@Override
	public Collection<? extends Player> getOnlinePlayers() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getConnection(Player player) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPing(Player player) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void respawnPlayer(Player player) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendActionbar(Player player, String message, int duration, Plugin instance) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendActionbar(Player player, String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendLinkedMessage(Player player, String message, String link) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendPacket(Player player, Object packet) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendTablist(Player player, String header, String footer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendTitle(Player player, String header, String footer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttributeSpeed(Player player, double value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setNametagVisibility(Team team, boolean shown) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setArmorstandAttributes(Entity armorstand, boolean visible, boolean customNameVisible, boolean gravity,
			String customName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAi(LivingEntity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setXpCooldown(Player player, int cooldown) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteItemAnnotations(ItemStack item) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getNetworkManager(Player player) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLocale(Player player) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void forceClearWorlds() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Properties getServerProperties() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsAntiXray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAntiXrayEnabled(boolean enabled) {
		throw new UnsupportedOperationException();
	}
}
