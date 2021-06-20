package de.cuuky.cfw.version;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

class OneTwelveVersionAdapter extends OneNineVersionAdapter {
	
	private Method localeMethod;

	@Override
	protected void initLocale() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try {
			this.localeMethod = Class.forName("org.bukkit.craftbukkit." + VersionUtils.getNmsVersion() + ".entity.CraftPlayer").getDeclaredMethod("getLocale");
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void initTitle() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, NoSuchMethodException {
	}
	
	@Override
	public void sendTitle(Player player, String header, String footer) {
		player.sendTitle(header, footer, 10, 70, 20);
	}
	
	@Override
	public String getLocale(Player player) {
		try {
			return (String) this.localeMethod.invoke(player);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new Error();
		}
	}
}
