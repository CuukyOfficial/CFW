package de.cuuky.cfw.version.minecraftversion;

import org.bukkit.entity.Player;

import us.myles.ViaVersion.api.Via;

public class ViaVersionUtils {

	// Plugin Name: ViaVersion

	public static int getVersion(Player player) {
		return Via.getAPI().getPlayerVersion(player);
	}
}
