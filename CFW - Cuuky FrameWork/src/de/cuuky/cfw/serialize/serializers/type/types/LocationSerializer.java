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

		Number x = (Number) section.get(key + ".x"), y = (Number) section.get(key + ".y"), z = (Number) section.get(key + ".z");

		// Compatibility
		try {
			Number yaw = (Number) section.get(key + ".yaw"), pitch = (Number) section.get(key + ".pitch");
			return new Location(manager.getOwnerInstance().getServer().getWorld(section.getString(key + ".world")), x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw.floatValue(), pitch.floatValue());
		} catch (Exception e) {}

		return new Location(manager.getOwnerInstance().getServer().getWorld(section.getString(key + ".world")), x.doubleValue(), y.doubleValue(), z.doubleValue());
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
		section.set(saveUnder + ".yaw", loc.getYaw());
		section.set(saveUnder + ".pitch", loc.getPitch());
		return true;
	}
}