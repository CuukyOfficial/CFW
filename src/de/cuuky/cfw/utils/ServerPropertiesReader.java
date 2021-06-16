package de.cuuky.cfw.utils;

import de.cuuky.cfw.version.BukkitVersion;
import de.cuuky.cfw.version.VersionUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;

public class ServerPropertiesReader {

    private HashMap<String, String> configuration;

    public ServerPropertiesReader() {
        this.configuration = new HashMap<>();

        this.readProperties();
    }

    private Object getFieldValue(Field field, Object object) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(object);
    }

    private Properties getProperties() {
        try {
            Object properties = null;
            if(VersionUtils.getVersion().isLowerThan(BukkitVersion.ONE_17)) {
            	Class<?> mcServerClass = Class.forName(VersionUtils.getNmsClass() + ".MinecraftServer");
                Object mcServer = mcServerClass.getMethod("getServer").invoke(null);
                Object propertyM = getFieldValue(mcServer.getClass().getField("propertyManager"), mcServer);
                properties = getFieldValue(propertyM.getClass().getDeclaredField("properties"), propertyM);
                if (!(properties instanceof Properties)) {
                    properties = getFieldValue(properties.getClass().getField("properties"), properties);
                }
            } else {
                Method dedicatedServerPropMethod = Bukkit.getServer().getClass().getDeclaredMethod("getProperties");
                dedicatedServerPropMethod.setAccessible(true);
                Object dedicatedServerProp = dedicatedServerPropMethod.invoke(Bukkit.getServer());
                Field[] fields = dedicatedServerProp.getClass().getFields();
                for (Field field : fields) {
                    if (!field.getType().equals(Properties.class))
                        continue;

                    field.setAccessible(true);
                    properties = field.get(dedicatedServerProp);
                    break;
                }
            }

            return (Properties) properties;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void readProperties() {
        Properties properties = this.getProperties();
        for (String key : properties.stringPropertyNames())
            this.configuration.put(key, properties.getProperty(key));
    }

    public HashMap<String, String> getConfiguration() {
        return this.configuration;
    }
}