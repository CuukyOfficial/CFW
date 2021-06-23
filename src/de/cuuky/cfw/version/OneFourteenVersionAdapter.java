package de.cuuky.cfw.version;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class OneFourteenVersionAdapter extends OneThirteenVersionAdapter {

	@Override
	public Properties getServerProperties() {
		try {
			Class<?> mcServerClass = Class.forName(VersionUtils.getNmsClass() + ".MinecraftServer");
			Object mcServer = mcServerClass.getMethod("getServer").invoke(null);

			Field serverSettingsField = mcServer.getClass().getField("propertyManager");
			serverSettingsField.setAccessible(true);
			Object serverSettings = serverSettingsField.get(mcServer);

			Field serverPropertiesField = serverSettings.getClass().getDeclaredField("properties");
			serverPropertiesField.setAccessible(true);
			Object serverProperties = serverPropertiesField.get(serverSettings);

			Field propertiesField = serverProperties.getClass().getField("properties");
			propertiesField.setAccessible(true);

			return (Properties) propertiesField.get(serverProperties);
		} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | InvocationTargetException | NoSuchMethodException e) {
			throw new Error(e);
		}
	}
}
