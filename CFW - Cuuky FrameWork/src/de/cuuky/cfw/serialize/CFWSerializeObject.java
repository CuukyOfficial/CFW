package de.cuuky.cfw.serialize;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import de.cuuky.cfw.serialize.field.FieldLoader;
import de.cuuky.cfw.serialize.identifier.CFWSerializeable;
import de.cuuky.cfw.serialize.serializer.CFWDeserializer;
import de.cuuky.cfw.serialize.serializer.CFWSerializer;

public class CFWSerializeObject {

	/*
	 * Pls dont look too close at this class. I made this withing like 4 hours
	 * and it works so I don't wanna change anything. I know, this is pretty
	 * ugly and too much code in one class but I'm really happy it works like
	 * this
	 */

	private CFWSerializeManager manager;

	private Class<? extends CFWSerializeable> clazz;
	private YamlConfiguration configuration;

	private FieldLoader fieldLoader;
	private File file;

	public CFWSerializeObject(CFWSerializeManager handler, Class<? extends CFWSerializeable> clazz) {
		this.clazz = clazz;
		this.fieldLoader = new FieldLoader(clazz);

		manager.addHandler(this);
	}

	public CFWSerializeObject(CFWSerializeManager manager, Class<? extends CFWSerializeable> clazz, String fileName) {
		this(manager, clazz);

		if (manager.getFile(fileName) != null) {
			this.file = manager.getFile(fileName);
			this.configuration = manager.getConfiguration(fileName);
		} else {
			this.file = new File("plugins/Varo", fileName);
			manager.addFile(fileName, file);
			this.configuration = YamlConfiguration.loadConfiguration(file);
			manager.addConfiguration(fileName, configuration);
		}
	}

	protected void clearOld() {
		Map<String, Object> configValues = configuration.getValues(false);
		for (Map.Entry<String, Object> entry : configValues.entrySet())
			configuration.set(entry.getKey(), null);
	}

	protected void load() {
		for (String string : configuration.getKeys(true)) {
			Object obj = configuration.get(string);
			if (obj instanceof MemorySection) {
				if (string.contains("."))
					continue;

				new CFWDeserializer(manager, (MemorySection) obj, this).deserialize();
			}
		}
	}

	protected void save(String saveUnder, CFWSerializeable instance, YamlConfiguration saveTo) {
		try {
			new CFWSerializer(manager, saveUnder, instance, saveTo);
		} catch (NoClassDefFoundError e) {
			System.out.println(manager.getFramework().getConsolePrefix() + "Failed to save files - did you change the version while the server was running?");
		}
	}

	protected void saveFile() {
		try {
			configuration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Class<? extends CFWSerializeable> getClazz() {
		return clazz;
	}

	public YamlConfiguration getConfiguration() {
		return configuration;
	}

	public FieldLoader getFieldLoader() {
		return fieldLoader;
	}

	public void onSave() {}
}