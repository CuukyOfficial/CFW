package de.cuuky.cfw.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitUtils {

	public static void saveTeleport(Player player, Location location) {
		while (!location.getChunk().isLoaded())
			location.getChunk().load();

		Location blockunder = location.clone().add(0, -1, 0);
		player.sendBlockChange(blockunder, blockunder.getBlock().getType(), (byte) 1);
		player.teleport(location.clone().add(0, 1, 0));
	}

    public static Player getPlayer(String name) throws Exception {
        return Bukkit.getPlayer(getUUID(name));
    }

    public static UUID getUUID(String name) throws Exception {
        Player player = Bukkit.getPlayer(name);
        return player == null ? UUIDUtils.getUUID(name) : player.getUniqueId();
    }
}