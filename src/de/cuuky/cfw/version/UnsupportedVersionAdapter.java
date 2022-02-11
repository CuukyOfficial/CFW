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
    public void sendClickableMessage(Player player, String message, ClickEvent.Action action, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
	public void sendLinkedMessage(Player player, String message, String link) {
		this.sendClickableMessage(player, message, null, link);
	}

	@Override
	public void sendTablist(Player player, String header, String footer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendTitle(Player player, String title, String subtitle) {
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
	public BlockFace getSignAttachedFace(Block block) {
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
	public void setServerProperty(String key, Object value) {
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
