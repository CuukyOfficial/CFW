package de.cuuky.cfw.serialize.serializers.type.types;

import java.lang.reflect.Field;

import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

public class NumberSerializer extends CFWSerializeType {

	public NumberSerializer(CFWSerializeManager manager) {
		super(manager);
	}

	@Override
	public Object deserialize(CFWSerializeable instance, String key, Field field, ConfigurationSection section) {
		Object object = section.get(key);
		if (field.getType().isAssignableFrom(Long.TYPE) || field.getType().isAssignableFrom(Long.class))
			return ((Number) object).longValue();

		if (field.getType().isAssignableFrom(Short.TYPE) || field.getType().isAssignableFrom(Short.class))
			return ((Number) object).shortValue();

		if (field.getType().isAssignableFrom(Float.TYPE) || field.getType().isAssignableFrom(Float.class))
			return ((Number) object).floatValue();

		if (field.getType().isAssignableFrom(Double.TYPE) || field.getType().isAssignableFrom(Double.class))
			return ((Number) object).doubleValue();

		if (field.getType().equals(Byte.TYPE) || field.getType().isAssignableFrom(Byte.class))
			return ((Number) object).byteValue();

		return null;
	}

	@Override
	public boolean serialize(CFWSerializeable instance, Field field, Object value, String saveUnder, ConfigurationSection section) {
		if (!(value instanceof Long) && !(value instanceof Short) && !(value instanceof Float) && !(value instanceof Double) && !(value instanceof Byte))
			return false;

		section.set(saveUnder, value);
		return true;
	}
}