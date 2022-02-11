package de.cuuky.cfw.version;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.cuuky.cfw.version.minecraft.MinecraftVersion;
import de.cuuky.cfw.version.minecraft.utils.ProtocolSupportUtils;
import de.cuuky.cfw.version.minecraft.utils.ViaVersionUtils;

public class VersionUtils {
	
	private static final String FORGE_CLASS = "net.minecraftforge.common.MinecraftForge";

	private static final String nmsClass;
	private static final String nmsVersion;
	private static final boolean forgeSupport;
	@Deprecated()
	private static Object spigot;

	private final static BukkitVersion version;
	private final static ServerSoftware serverSoftware;
	private final static VersionAdapter versionAdapter;
	private final static Map<Player, MinecraftVersion> playerVersions;

	static {
		forgeSupport = isClassPresent(FORGE_CLASS);
		playerVersions = new HashMap<>();

		if (Bukkit.getServer() == null) {
			version = BukkitVersion.UNSUPPORTED;
			serverSoftware = ServerSoftware.UNKNOWN;
			nmsClass = null;
			nmsVersion = null;
		} else {

			String base = "net.minecraft";
			nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			if (nmsVersion.startsWith("v1")) {
				// 1.7 - 1.16
				nmsClass = base + ".server." + nmsVersion;
			} else {
				// Thermos (1.17+ does not use this string at all)
				nmsClass = base + ".server";
			}
			version = BukkitVersion.getVersion(nmsVersion);
			serverSoftware = ServerSoftware.getServerSoftware();

			try {
				spigot = Bukkit.getServer().getClass().getDeclaredMethod("spigot").invoke(Bukkit.getServer());
			} catch (Exception e) {
			}
		}
		versionAdapter = serverSoftware.getVersionAdapter(version.getAdapterSupplier());
	}
	
	/**
	 * @param clazz Class you want to check
	 * @return Whether the provided class is loaded
	 */
	static boolean isClassPresent(String clazz) {
		try {
			Class.forName(clazz);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * Use {@link VersionAdapter#setServerProperty} instead
	 * 
	 * @param key
	 * @param value
	 * @deprecated Use {@link VersionAdapter#setServerProperty} instead
	 */
	public static void setMinecraftServerProperty(String key, Object value) {
		versionAdapter.setServerProperty(key, value);
	}

	@Deprecated
	public static double getHearts(Player player) {
		return player.getHealth();
	}

	static String getNmsClass() {
		return nmsClass;
	}

	public static String getNmsVersion() {
		return nmsVersion;
	}

	/**
	 * @return Whether the software has support for Forge mods
	 */
	public static boolean hasForgeSupport() {
		return forgeSupport;
	}

	/**
	 * Use {@link VersionAdapter} instead
	 * 
	 * @return
	 */
	@Deprecated()
	public static Object getSpigot() {
		return spigot;
	}

	public static MinecraftVersion getMinecraftVersion(Player player) {
		MinecraftVersion version = playerVersions.get(player);
		if (version != null)
			return version;

		int protocolId = -1;
		if (ViaVersionUtils.isAvailable())
			protocolId = ViaVersionUtils.getVersion(player);
		else if (ProtocolSupportUtils.isAvailable())
			protocolId = ProtocolSupportUtils.getVersion(player);
		else
			System.err.println("[CFW] Cannot get version of player without protocolsupport or viaversion installed");

		playerVersions.put(player, version = MinecraftVersion.getMinecraftVersion(protocolId));
		return version;
	}

	public static BukkitVersion getVersion() {
		return version;
	}

	public static VersionAdapter getVersionAdapter() {
		return versionAdapter;
	}

	public static ServerSoftware getServerSoftware() {
		return serverSoftware;
	}
}