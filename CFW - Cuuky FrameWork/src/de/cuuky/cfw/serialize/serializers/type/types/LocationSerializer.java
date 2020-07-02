package de.cuuky.cfw.serialize.serializers.type.types;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

public class LocationSerializer extends CFWSerializeType {

	public LocationSerializer(CFWSerializeManager manager) {
		super(manager);
	}

	@Override
	public Object deserialize(CFWSerializeable instance, String key, Field field, ConfigurationSection section) {
		if (field.getType() != Location.class || manager.getOwnerInstance().getServer().getWorld(section.getString(key + ".world")) == null)
			return null;

		return new Location(manager.getOwnerInstance().getServer().getWorld(section.getString(key + ".world")), (double) section.get(key + ".x"), (double) section.get(key + ".y"), (double) section.get(key + ".z"));
	}

	@Override
	public boolean serialize(CFWSerializeable instance, Field field, Object value, String saveUnder, ConfigurationSection section) {
		if (field.getType() != Location.class)
			return false;

		Location loc = (Location) value;
		section.set(saveUnder + ".world", loc.getWorld().getName());
		section.set(saveUnder + ".x", loc.getX());
		section.set(saveUnder + ".y", loc.getY());
		section.set(saveUnder + ".z", loc.getZ());
		return true;
	}
}