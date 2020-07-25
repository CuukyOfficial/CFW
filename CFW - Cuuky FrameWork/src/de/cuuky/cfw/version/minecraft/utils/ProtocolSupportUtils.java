package de.cuuky.cfw.version.minecraft.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

public class ProtocolSupportUtils {

	// Plugin Name: ProtocolSupport

	private static Method getProtocolVersionMethod, getIdMethod;

	static {
		try {
			getProtocolVersionMethod = Class.forName("protocolsupport.api.ProtocolSupportAPI").getMethod("getProtocolVersion", Player.class);
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {}
	}

	public static int getVersion(Player player) {
		if (getProtocolVersionMethod == null)
			throw new NullPointerException("Could not find ProtocolSupportAPI");

		Object version;
		try {
			version = getProtocolVersionMethod.invoke(null, player);

			if (getIdMethod == null)
				getIdMethod = version.getClass().getMethod("getId");

			return (int) getIdMethod.invoke(version);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		return -1;
	}
	
	public static boolean isAvailable() {
		return getProtocolVersionMethod != null;
	}
}