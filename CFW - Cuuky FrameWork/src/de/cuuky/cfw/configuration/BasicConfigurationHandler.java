package de.cuuky.cfw.configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class BasicConfigurationHandler {

	private File file;
	private YamlConfiguration configuration;

	public BasicConfigurationHandler(String path) {
		this.file = new File(path);
		this.configuration = YamlConfiguration.loadConfiguration(this.file);

		this.configuration.options().copyDefaults(true);

		if(!this.file.exists())
			save();
	}

	public void save() {
		try {
			this.configuration.save(this.file);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void setValue(String path, Object object) {
		this.configuration.set(path, object);
	}

	public Object getValue(String name, Object defaultValue) {
		if(this.configuration.contains(name))
			return this.configuration.get(name);
		else {
			this.configuration.addDefault(name, defaultValue);
			save();
		}
		
		return defaultValue;
	}

	public boolean getBool(String name, boolean defaultValue) {
		return (boolean) getValue(name, defaultValue);
	}

	public String getString(String name, String defaultValue) {
		return (String) getValue(name, defaultValue);
	}
	
	public int getInt(String name, int defaultValue) {
		return (Integer) getValue(name, defaultValue);
	}
}