package de.cuuky.cfw.version;

import de.cuuky.cfw.version.minecraft.MinecraftVersion;
import de.cuuky.cfw.version.minecraft.utils.ProtocolSupportUtils;
import de.cuuky.cfw.version.minecraft.utils.ViaVersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VersionUtils {

	private static String nmsClass;
	private static String nmsVersion;
	@Deprecated()
	private static Object spigot;

	private final static BukkitVersion version;
	private final static ServerSoftware serverSoftware;
	private final static VersionAdapter versionAdapter;
	private final static Map<Player, MinecraftVersion> playerVersions;

	static {
		playerVersions = new HashMap<>();

		if (Bukkit.getServer() == null) {
			version = BukkitVersion.UNSUPPORTED;
			serverSoftware = ServerSoftware.UNKNOWN;
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
			serverSoftware = ServerSoftware.getServerSoftware(Bukkit.getVersion(), Bukkit.getName());

			try {
				spigot = Bukkit.getServer().getClass().getDeclaredMethod("spigot").invoke(Bukkit.getServer());
			} catch (Exception e) {
			}
		}
		versionAdapter = serverSoftware.getVersionAdapter(version.getAdapterSupplier());
	}

	public static void setMinecraftServerProperty(String key, Object value) {
		try {
			Class<?> serverClass = Class.forName(VersionUtils.getNmsClass() + ".MinecraftServer");
			Object server = serverClass.getMethod("getServer").invoke(null);
			Object manager = server.getClass().getField("propertyManager").get(server);
			Method method = manager.getClass().getMethod("setProperty", String.class, Object.class);
			method.invoke(manager, key, value);
		} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException
				| ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

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
	 * Use {@link VersionAdapter} instead
	 * 
	 * @return
	 */
	@Deprecated()
	public static Object getSpigot() {
		return spigot;
	}

	/**
	 * Use {@link VersionAdapter#getOnlinePlayers()} instead
	 * 
	 * @return
	 */
	@Deprecated()
	public static ArrayList<Player> getOnlinePlayer() {
		return new ArrayList<>(Bukkit.getOnlinePlayers());
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