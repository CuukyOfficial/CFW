package de.cuuky.cfw.configuration;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class BasicConfigurationHandler {

    private final File file;
    private final YamlConfiguration configuration;

    public BasicConfigurationHandler(String path) {
        this.file = new File(path);
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        this.configuration.options().copyDefaults(true);
        if (!this.file.exists()) this.save();
    }

    public void save() {
        try {
            this.configuration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setValue(String path, Object object) {
        this.configuration.set(path, object);
    }

    public Object getValue(String name, Object defaultValue) {
        if (this.configuration.contains(name)) return this.configuration.get(name);

        this.configuration.addDefault(name, defaultValue);
        this.save();
        return defaultValue;
    }

    public boolean getBool(String name, boolean defaultValue) {
        return (boolean) getValue(name, defaultValue);
    }

    public String getString(String name, String defaultValue) {
        return (String) getValue(name, defaultValue);
    }

    public String getColoredString(String name, String defaultValue) {
        return ChatColor.translateAlternateColorCodes('&', getString(name, defaultValue));
    }

    public int getInt(String name, int defaultValue) {
        return (Integer) getValue(name, defaultValue);
    }
}