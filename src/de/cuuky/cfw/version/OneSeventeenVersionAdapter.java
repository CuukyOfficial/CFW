package de.cuuky.cfw.version;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

class OneSeventeenVersionAdapter extends OneSixteenVersionAdapter {

	@Override
	protected void initPlayer()
			throws NoSuchMethodException, SecurityException, NoSuchFieldException, ClassNotFoundException {
	}
	
	protected void initXp() {
		this.initXp("net.minecraft.world.entity.player.EntityHuman", "net.minecraft.world.food.FoodMetaData");
	}
	
	@Override
	public int getPing(Player player) {
		return player.getPing();
	}

	@Override
	public void respawnPlayer(Player player) {
		throw new Error("No implemented yet");
	}

	@Override
	protected String getWorldServerFieldName() {
		return "R";
	}

	@Override
	public Properties getServerProperties() {
		try {
			Method dedicatedServerPropMethod = Bukkit.getServer().getClass().getDeclaredMethod("getProperties");
			dedicatedServerPropMethod.setAccessible(true);
			Object dedicatedServerProp = dedicatedServerPropMethod.invoke(Bukkit.getServer());
			Field[] fields = dedicatedServerProp.getClass().getFields();
			for (Field field : fields) {
				if (!field.getType().equals(Properties.class))
					continue;

				field.setAccessible(true);
				return (Properties) field.get(dedicatedServerProp);
			}
			throw new Error("missing properties field");
		}catch(NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new Error(e);
		}
	}
}
