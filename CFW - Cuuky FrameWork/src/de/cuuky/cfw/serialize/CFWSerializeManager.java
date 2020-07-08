package de.cuuky.cfw.serialize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.loader.FieldLoader;
import de.cuuky.cfw.serialize.serializers.CFWDeserializer;
import de.cuuky.cfw.serialize.serializers.CFWSerializer;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;
import de.cuuky.cfw.serialize.serializers.type.types.CFWSerializeableSerializer;
import de.cuuky.cfw.serialize.serializers.type.types.CollectionSerializer;
import de.cuuky.cfw.serialize.serializers.type.types.EnumSerializer;
import de.cuuky.cfw.serialize.serializers.type.types.LocationSerializer;
import de.cuuky.cfw.serialize.serializers.type.types.LongSerializer;
import de.cuuky.cfw.serialize.serializers.type.types.MapSerializer;

public class CFWSerializeManager extends FrameworkManager {

	private HashMap<Class<?>, FieldLoader> loaded;

	private ArrayList<CFWSerializeType> serializer;

	public CFWSerializeManager(JavaPlugin instance) {
		super(FrameworkManagerType.SERIALIZE, instance);

		this.loaded = new HashMap<Class<?>, FieldLoader>();
		this.serializer = new ArrayList<CFWSerializeType>();

		this.serializer.add(new CFWSerializeableSerializer(this));
		this.serializer.add(new MapSerializer(this));
		this.serializer.add(new LongSerializer(this));
		this.serializer.add(new LocationSerializer(this));
		this.serializer.add(new EnumSerializer(this));
		this.serializer.add(new CollectionSerializer(this));
	}

	public ArrayList<CFWSerializeable> loadSerializeables(Class<? extends CFWSerializeable> clazz, File file, Object invokeObject) {
		ArrayList<CFWSerializeable> serializeables = new ArrayList<CFWSerializeable>();
		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

		for (String s : configuration.getKeys(true)) {
			if (!configuration.isConfigurationSection(s) || s.contains("."))
				continue;

			serializeables.add(new CFWDeserializer(this, configuration.getConfigurationSection(s), invokeObject, clazz).deserialize());
		}

		return serializeables;
	}

	public void saveSerializeable(String key, CFWSerializeable serializeable, YamlConfiguration configuration) {
		new CFWSerializer(this, configuration.createSection(key), serializeable).serialize();
	}

	public <T> void saveFiles(Class<? extends CFWSerializeable> clazz, ArrayList<T> list, File file, SaveVisit<T> visit) {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

		for (T serializeable : list)
			saveSerializeable(visit.onKeySave(serializeable), (CFWSerializeable) serializeable, config);

		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FieldLoader loadClass(Class<?> clazz) {
		if (loaded.containsKey(clazz))
			return loaded.get(clazz);
		else {
			FieldLoader loader = new FieldLoader(clazz);
			this.loaded.put(clazz, loader);
			return loader;
		}
	}

	public ArrayList<CFWSerializeType> getSerializer() {
		return serializer;
	}

	public String getNullReplace() {
		return "nullReplace";
	}

	public static abstract class SaveVisit<T> {

		public abstract String onKeySave(T object);

	}
}