package de.cuuky.cfw.serialize.serializers.type.types;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.loader.FieldLoader;
import de.cuuky.cfw.serialize.serializers.CFWDeserializer;
import de.cuuky.cfw.serialize.serializers.CFWSerializer;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

public class CollectionSerializer extends CFWSerializeType {

	public CollectionSerializer(CFWSerializeManager manager) {
		super(manager);
	}

	@Override
	public Object deserialize(CFWSerializeable instance, String key, Field field, ConfigurationSection section) {
		if (!Collection.class.isAssignableFrom(field.getType()) || !section.isConfigurationSection(key))
			return null;

		FieldLoader loader = manager.loadClass(field.getType());
		Class<? extends CFWSerializeable> collectionClazz = loader.getArrayTypes().get(field);
		if (collectionClazz == null)
			return null;

		ArrayList<CFWSerializeable> content = new ArrayList<CFWSerializeable>();
		ConfigurationSection arraySection = section.getConfigurationSection(key);
		for (String arrayKey : arraySection.getKeys(true)) {
			Object entry = arraySection.get(arrayKey);
			if (!arraySection.isConfigurationSection(arrayKey) || arrayKey.contains("."))
				continue;

			content.add(new CFWDeserializer(manager, (ConfigurationSection) entry, instance, collectionClazz).deserialize());
		}
		return content;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean serialize(CFWSerializeable instance, Field field, Object value, String saveUnder, ConfigurationSection section) {
		if (!(value instanceof List))
			return false;

		FieldLoader loader = manager.loadClass(instance.getClass());
		if (loader.getArrayTypes().get(field) == null)
			return false;

		ArrayList<CFWSerializeable> list = (ArrayList<CFWSerializeable>) value;
		for (int i = 0; i < list.size(); i++) {
			new CFWSerializer(manager, section.createSection(saveUnder).createSection(String.valueOf(i)), list.get(i)).serialize();
		}
		return true;
	}
}