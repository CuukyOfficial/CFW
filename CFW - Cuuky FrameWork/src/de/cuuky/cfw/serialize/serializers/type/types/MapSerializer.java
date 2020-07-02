package de.cuuky.cfw.serialize.serializers.type.types;

import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

public class MapSerializer extends CFWSerializeType {

	public MapSerializer(CFWSerializeManager manager) {
		super(manager);
	}

	@Override
	public Object deserialize(CFWSerializeable instance, String key, Field field, ConfigurationSection section) {
		if (!Map.class.isAssignableFrom(field.getType()))
			return null;

		return section.getConfigurationSection(key).getValues(false);
	}

	@Override
	public boolean serialize(CFWSerializeable instance, Field field, Object value, String saveUnder, ConfigurationSection section) {
		return false;
	}
}