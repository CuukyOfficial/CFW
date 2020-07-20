package de.cuuky.cfw.version.minecraftversion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public enum MinecraftVersion {

	// Protocol version numbers: https://wiki.vg/Protocol_version_numbers

	MINECRAFT_1_16_1(736, "1.16.1"),
	MINECRAFT_1_16(735, "1.16"),
	MINECRAFT_1_15_2(578, "1.15.2"),
	MINECRAFT_1_15_1(575, "1.15.1"),
	MINECRAFT_1_15(573, "1.15"),
	MINECRAFT_1_14_4(498, "1.14.4"),
	MINECRAFT_1_14_3(490, "1.14.3"),
	MINECRAFT_1_14_2(485, "1.14.2"),
	MINECRAFT_1_14_1(480, "1.14.1"),
	MINECRAFT_1_14(477, "1.14"),
	MINECRAFT_1_13_2(404, "1.13.2"),
	MINECRAFT_1_13_1(401, "1.13.1"),
	MINECRAFT_1_13(393, "1.13"),
	MINECRAFT_1_12_2(340, "1.12.2"),
	MINECRAFT_1_12_1(338, "1.12.1"),
	MINECRAFT_1_12(335, "1.12"),
	MINECRAFT_1_11_1(316, "1.11.2"),
	MINECRAFT_1_11(315, "1.11"),
	MINECRAFT_1_10(210, "1.10"),
	MINECRAFT_1_9_4(110, "1.9.3-1.9.4"),
	MINECRAFT_1_9_2(109, "1.9.2"),
	MINECRAFT_1_9_1(108, "1.9.1"),
	MINECRAFT_1_9(107, "1.9"),
	MINECRAFT_1_8(47, "1.8-1.8.9"),
	MINECRAFT_1_7_10(5, "1.7.6-1.7.10"),
	MINECRAFT_1_7_2(4, "1.7.2-1.7.5"),
	MINECRAFT_UNKNOWN(-1, "Unknown");

	private int protocolID;
	private String name;

	private MinecraftVersion(int ID, String name) {
		this.name = name;
		this.protocolID = ID;
	}

	public String getName() {
		return name;
	}

	public int getProtocolID() {
		return protocolID;
	}

	public static MinecraftVersion getMinecraftVersion(int ProtocolID) {
		MinecraftVersion version = MINECRAFT_UNKNOWN;
		for (MinecraftVersion mcver : MinecraftVersion.values())
			if (mcver.getProtocolID() == ProtocolID)
				version = mcver;
		return version;
	}

	public static MinecraftVersion getMinecraftVersion(Player player) {
		int id = -1;
		if (Bukkit.getServer().getPluginManager().getPlugin("ViaVersion") != null)
			id = ViaVersionUtils.getVersion(player);
		else if (Bukkit.getServer().getPluginManager().getPlugin("ProtocolSupport") != null)
			id = ProtocolSupportUtils.getVersion(player);
		else
			System.out.println("[CFW] You have to install ViaVersion or ProtocolSupport in order to get the version of a player.");
		return getMinecraftVersion(id);
	}

}
