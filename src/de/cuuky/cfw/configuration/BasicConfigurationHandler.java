package de.cuuky.cfw.configuration;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration

public class BasicConfigurationHandler {

    private final BetterYamlConfiguration configuration;

    public BasicConfigurationHandler(String path) {
        this.configuration = new BetterYamlConfiguration(path);
        this.configuration.options().copyDefaults(true);
        if (!this.configuration.getFile().exists()) this.save();
    }

    public void save() {
        this.configuration.save();
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
    
    public YamlConfiguration getYamlConfig() {
        return this.configuration;
    }
}
