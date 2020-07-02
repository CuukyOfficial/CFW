package de.cuuky.cfw.serialize.serializers.type;

import java.lang.reflect.Field;

import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;

public abstract class CFWSerializeType {

	protected CFWSerializeManager manager;

	public CFWSerializeType(CFWSerializeManager manager) {
		this.manager = manager;
	}

	public abstract Object deserialize(CFWSerializeable instance, String key, Field field, ConfigurationSection section);

	public abstract boolean serialize(CFWSerializeable instance, Field field, Object value, String saveUnder, ConfigurationSection section);

}