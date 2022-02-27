package de.cuuky.cfw.utils;

import java.util.HashMap;
import java.util.Properties;

import de.cuuky.cfw.version.VersionUtils;

@Deprecated
public class ServerPropertiesReader {

    private HashMap<String, String> configuration;

    public ServerPropertiesReader() {
        this.configuration = new HashMap<>();

        this.readProperties();
    }

    private void readProperties() {
        Properties properties = VersionUtils.getVersionAdapter().getServerProperties();
        for (String key : properties.stringPropertyNames())
            this.configuration.put(key, properties.getProperty(key));
    }

    public HashMap<String, String> getConfiguration() {
        return this.configuration;
    }
}