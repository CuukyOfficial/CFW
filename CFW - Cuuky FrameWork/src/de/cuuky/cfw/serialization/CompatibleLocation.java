package de.cuuky.cfw.serialization;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class CompatibleLocation implements ConfigurationSerializable {

	private Location location;

	public CompatibleLocation(Location location) {
		this.location = location;
	}

	public CompatibleLocation(String world, double x, double y, double z, float yaw, float pitch) {
		this.location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}

	public Location getLocation() {
		return this.location;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		map.put("world", location.getWorld().getName());
		map.put("x", location.getX());
		map.put("y", location.getY());
		map.put("z", location.getZ());
		map.put("yaw", location.getYaw());
		map.put("pitch", location.getPitch());

		return map;
	}

	public static CompatibleLocation deserialize(Map<String, Object> args) {
		return new CompatibleLocation((String) args.get("world"), (double) args.get("x"), (double) args.get("y"), (double) args.get("z"), (float) args.get("yaw"), (float) args.get("pitch"));
	}
}