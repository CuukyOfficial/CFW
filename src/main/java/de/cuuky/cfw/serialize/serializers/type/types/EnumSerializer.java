package de.cuuky.cfw.serialize.serializers.type.types;

import java.lang.reflect.Field;

import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.loader.FieldLoader;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

@Deprecated
public class EnumSerializer extends CFWSerializeType {

	public EnumSerializer(CFWSerializeManager manager) {
		super(manager);
	}

	@Override
	public Object deserialize(CFWSerializeable instance, String key, Field field, ConfigurationSection section) {
		Object object = section.get(key);
		if (!field.getType().isEnum() || !(object instanceof String) || !CFWSerializeable.class.isAssignableFrom(field.getType()))
			return null;

		@SuppressWarnings("unchecked")
		FieldLoader loader = manager.loadClass((Class<? extends CFWSerializeable>) field.getType());
		return manager.deserializeEnum(loader, object);
		// try {
		// return loader.getFields().get((String) object).get(null);
		// } catch (IllegalArgumentException | IllegalAccessException e) {
		// e.printStackTrace();
		// }
		//
		// return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean serialize(CFWSerializeable instance, Field field, Object value, String saveUnder, ConfigurationSection section) {
		if (!field.getType().isEnum() || !CFWSerializeable.class.isAssignableFrom(field.getType()))
			return false;

		FieldLoader loader = manager.loadClass((Class<? extends CFWSerializeable>) field.getType());
		section.set(saveUnder, manager.serializeEnum(loader, value));
		return true;
	}
}