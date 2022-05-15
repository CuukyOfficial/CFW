package de.cuuky.cfw.version;

import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.Bukkit;

public class OneSixteenVersionAdapter extends OneFourteenVersionAdapter {

	@Override
	public void forceClearWorlds() {
		try {
			Field dedicatedServerField = Class.forName("org.bukkit.craftbukkit." + VersionUtils.getNmsVersion() + ".CraftServer").getDeclaredField("console");
			dedicatedServerField.setAccessible(true);
			Object dedicatedServer = dedicatedServerField.get(Bukkit.getServer());
			Map<?, ?> worldServer = (Map<?, ?>) dedicatedServer.getClass().getField(this.getWorldServerFieldName()).get(dedicatedServer);
			worldServer.clear();
		}catch(ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new Error(e);
		}
	}
	
	protected String getWorldServerFieldName() {
		return "worldServer";
	}
}
