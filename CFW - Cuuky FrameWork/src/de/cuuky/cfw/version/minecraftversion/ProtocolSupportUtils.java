package de.cuuky.cfw.version.minecraftversion;

import org.bukkit.entity.Player;

import protocolsupport.api.ProtocolSupportAPI;

public class ProtocolSupportUtils {

	// Plugin Name: ProtocolSupport
	
	public static int getVersion(Player player) {
		return ProtocolSupportAPI.getProtocolVersion(player).getId();
	}

}
