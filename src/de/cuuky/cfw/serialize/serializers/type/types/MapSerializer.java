package de.cuuky.cfw.serialize.serializers.type.types;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.loader.FieldLoader;
import de.cuuky.cfw.serialize.serializers.CFWDeserializer;
import de.cuuky.cfw.serialize.serializers.CFWSerializer;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

public class MapSerializer extends CFWSerializeType {

	public MapSerializer(CFWSerializeManager manager) {
		super(manager);
	}

	@Override
	public Object deserialize(CFWSerializeable instance, String key, Field field, ConfigurationSection section) {
		if (!Map.class.isAssignableFrom(field.getType()) || !section.isConfigurationSection(key))
			return null;

		FieldLoader loader = manager.loadClass(instance.getClass());
		Class<? extends CFWSerializeable> keyClazz = loader.getKeyType(field), valueClazz = loader.getValueType(field);
		if (keyClazz == null && valueClazz == null)
			return null;

		if (keyClazz != null)
			if (!Enum.class.isAssignableFrom(keyClazz))
				throw new Error("Cannot deserialize CFWSerialize class other than Enums as key");

		Map<Object, Object> content = new HashMap<>();
		ConfigurationSection arraySection = section.getConfigurationSection(key);
		for (String arrayKey : arraySection.getKeys(true)) {
			Object entry = arraySection.get(arrayKey);
			if (arrayKey.contains("."))
				continue;

			Object okey = arrayKey;
			if (keyClazz != null && Enum.class.isAssignableFrom(keyClazz))
				okey = manager.deserializeEnum(manager.loadClass(keyClazz), arrayKey);

			Object ovalue = entry;
			if (valueClazz != null)
				if (Enum.class.isAssignableFrom(valueClazz))
					ovalue = manager.deserializeEnum(manager.loadClass(valueClazz), entry);
				else
					ovalue = new CFWDeserializer(manager, (ConfigurationSection) entry, instance, valueClazz).deserialize();

			content.put(okey, ovalue);
		}

		return content;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean serialize(CFWSerializeable instance, Field field, Object value, String saveUnder, ConfigurationSection section) {
		if (!(value instanceof Map))
			return false;

		FieldLoader loader = manager.loadClass(instance.getClass());
		Class<? extends CFWSerializeable> keyClazz = loader.getKeyType(field), valueClazz = loader.getValueType(field);
		if (keyClazz == null && valueClazz == null)
			return false;

		if (keyClazz != null)
			if (!Enum.class.isAssignableFrom(keyClazz))
				throw new Error("Cannot deserialize CFWSerialize class other than Enums as key");

		Map<Object, Object> list = (Map<Object, Object>) value;
		ConfigurationSection mapSection = section.createSection(saveUnder);
		for (Object object : list.keySet()) {
			String path = object.toString();

			if (keyClazz != null && Enum.class.isAssignableFrom(keyClazz))
				path = manager.serializeEnum(manager.loadClass(keyClazz), object);

			Object listValue = list.get(object);
			if (valueClazz != null)
				if (Enum.class.isAssignableFrom(valueClazz))
					mapSection.set(String.valueOf(path), manager.serializeEnum(manager.loadClass(valueClazz), listValue));
				else
					new CFWSerializer(manager, mapSection.createSection(String.valueOf(path)), (CFWSerializeable) listValue).serialize();
		}

		return true;
	}
}