package de.cuuky.cfw.serialize.serializers.type.types;

import java.lang.reflect.Field;

import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.serializers.CFWDeserializer;
import de.cuuky.cfw.serialize.serializers.CFWSerializer;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

public class CFWSerializeableSerializer extends CFWSerializeType {

	public CFWSerializeableSerializer(CFWSerializeManager manager) {
		super(manager);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object deserialize(CFWSerializeable instance, String key, Field field, ConfigurationSection section) {
		if (!CFWSerializeable.class.isAssignableFrom(field.getType()) || !section.isConfigurationSection(key))
			return null;

		return new CFWDeserializer(manager, section.getConfigurationSection(key), instance, (Class<? extends CFWSerializeable>) field.getType()).deserialize();
	}

	@Override
	public boolean serialize(CFWSerializeable instance, Field field, Object value, String saveLocation, ConfigurationSection section) {
		if(!(value instanceof CFWSerializeable))
			return false;
		
		new CFWSerializer(manager, section.createSection(saveLocation), (CFWSerializeable) value).serialize();
		return true;
	}
}