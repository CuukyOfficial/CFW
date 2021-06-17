package de.cuuky.cfw.version;

import de.cuuky.cfw.version.minecraft.MinecraftVersion;
import de.cuuky.cfw.version.minecraft.utils.ProtocolSupportUtils;
import de.cuuky.cfw.version.minecraft.utils.ViaVersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VersionUtils {

	private static String nmsClass;
	private static String nmsVersion;
	private static Object spigot;

	private static BukkitVersion version;
	private static ServerSoftware serverSoftware;
	private static Map<Player, MinecraftVersion> playerVersions;

	static {
		playerVersions = new HashMap<>();

		String base = "net.minecraft";
		nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			//1.7 - 1.16
			nmsClass = base + ".server." + nmsVersion;
			Class.forName(nmsClass + ".MinecraftServer");
		} catch (ClassNotFoundException e) {
			//1.17+
			nmsClass = base;
		}
		version = BukkitVersion.getVersion(nmsVersion);
		serverSoftware = ServerSoftware.getServerSoftware(Bukkit.getVersion(), Bukkit.getName());

		try {
			spigot = Bukkit.getServer().getClass().getDeclaredMethod("spigot").invoke(Bukkit.getServer());
		} catch (Exception e) {
		}
	}

	public static void setMinecraftServerProperty(String key, Object value) {
		try {
			Class<?> serverClass = Class.forName(VersionUtils.getNmsClass() + ".MinecraftServer");
			Object server = serverClass.getMethod("getServer").invoke(null);
			Object manager = server.getClass().getField("propertyManager").get(server);
			Method method = manager.getClass().getMethod("setProperty", String.class, Object.class);
			method.invoke(manager, key, value);
		} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static double getHearts(Player player) {
		return ((Damageable) player).getHealth();
	}

	public static String getNmsClass() {
		return nmsClass;
	}

	public static String getNmsVersion() {
		return nmsVersion;
	}
	
	public static Object getSpigot() {
		return spigot;
	}

	public static ArrayList<Player> getOnlinePlayer() {
		ArrayList<Player> list = new ArrayList<Player>();
		for (Player player : Bukkit.getOnlinePlayers())
			list.add(player);

		return list;
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

	public static ServerSoftware getServerSoftware() {
		return serverSoftware;
	}
}