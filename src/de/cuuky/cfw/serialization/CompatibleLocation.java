package de.cuuky.cfw.serialization;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

@Deprecated
public class CompatibleLocation extends Location implements ConfigurationSerializable {

    public CompatibleLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public CompatibleLocation(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    public CompatibleLocation(Location location) {
        this(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Deprecated
	public Location getLocation() {
		return this;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		map.put("world", this.getWorld().getName());
		map.put("x", this.getX());
		map.put("y", this.getY());
		map.put("z", this.getZ());
		map.put("yaw", this.getYaw());
		map.put("pitch", this.getPitch());

		return map;
	}

    @Override
    public CompatibleLocation clone() {
        return (CompatibleLocation) super.clone();
    }

    public static CompatibleLocation deserialize(Map<String, Object> args) {
		Number x = (Number) args.get("x"), y = (Number) args.get("y"), z = (Number) args.get("z"), yaw = (Number) args.get("yaw"), pitch = (Number) args.get("pitch");
        String world = (String) args.get("world");
		return new CompatibleLocation(Bukkit.getWorld(world), x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw.floatValue(), pitch.floatValue());
	}
}