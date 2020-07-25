package de.cuuky.cfw.version.minecraft.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

public class ViaVersionUtils {

	// Plugin Name: ViaVersion

	private static Object api;
	private static Method getPlayerVersionMethod;

	static {
		try {
			api = Class.forName("us.myles.ViaVersion.api.Via").getMethod("getAPI").invoke(null);
			getPlayerVersionMethod = api.getClass().getMethod("getPlayerVersion", Player.class);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {}
	}

	public static int getVersion(Player player) {
		if (getPlayerVersionMethod == null)
			throw new NullPointerException("Could not find VIA API");

		try {
			return (int) getPlayerVersionMethod.invoke(api, player);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static boolean isAvailable() {
		return getPlayerVersionMethod != null;
	}
}