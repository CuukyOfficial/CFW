package de.cuuky.cfw.utils;

import de.cuuky.cfw.version.VersionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

public class ServerPropertiesReader {

    private HashMap<String, String> configuration;

    public ServerPropertiesReader() {
        this.configuration = new HashMap<>();

        readProperties();
    }

    private Properties getProperties() {
        try {
            Class<?> mcServerClass = Class.forName(VersionUtils.getNmsClass() + ".MinecraftServer");
            Object mcServer = mcServerClass.getMethod("getServer").invoke(null);
            Object propertyM = mcServer.getClass().getMethod("getPropertyManager").invoke(mcServer);
            Object properties = propertyM.getClass().getField("properties").get(propertyM);
            return (Properties) properties;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
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