package de.cuuky.cfw.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BukkitUtils {

	public static void saveTeleport(Player player, Location location) {
		while (!location.getChunk().isLoaded())
			location.getChunk().load();

		Location blockunder = location.clone().add(0, -1, 0);
		player.sendBlockChange(blockunder, blockunder.getBlock().getType(), (byte) 1);
		player.teleport(location.clone().add(0, 1, 0));
	}
}