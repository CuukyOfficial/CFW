package de.cuuky.cfw.clientadapter.board.scoreboard;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class FakeOfflinePlayer implements OfflinePlayer {

	private String name;

	public FakeOfflinePlayer(String name) {
		this.name = name;
	}

	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	public void setOp(boolean arg0) {}

	@Override
	public Map<String, Object> serialize() {
		return null;
	}

	@Override
	public Location getBedSpawnLocation() {
		return null;
	}

	@Override
	public long getFirstPlayed() {
		return 0;
	}

	@Override
	public long getLastPlayed() {
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Player getPlayer() {
		return null;
	}

	@Override
	public UUID getUniqueId() {
		return UUID.fromString("fbaa9566-6002-4359-b42a-f7235487f10c");
	}

	@Override
	public boolean hasPlayedBefore() {
		return false;
	}

	@Override
	public boolean isBanned() {
		return false;
	}

	@Override
	public boolean isOnline() {
		return false;
	}

	@Override
	public boolean isWhitelisted() {
		return false;
	}

	@Override
	public void setWhitelisted(boolean arg0) {}

	//@Override
	public void setBanned(boolean arg0) {}

	@Override
	public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {
	}

	@Override
	public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
	}

	@Override
	public void incrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
	}

	@Override
	public void decrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
	}

	@Override
	public void setStatistic(Statistic statistic, int newValue) throws IllegalArgumentException {
	}

	@Override
	public int getStatistic(Statistic statistic) throws IllegalArgumentException {
		return 0;
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
	}

	@Override
	public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
	}

	@Override
	public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
		return 0;
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
	}

	@Override
	public void decrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
	}

	@Override
	public void setStatistic(Statistic statistic, Material material, int newValue) throws IllegalArgumentException {
	}

	@Override
	public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
	}

	@Override
	public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
	}

	@Override
	public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
		return 0;
	}

	@Override
	public void incrementStatistic(Statistic statistic, EntityType entityType, int amount)
			throws IllegalArgumentException {
	}

	@Override
	public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
	}

	@Override
	public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
	}
}