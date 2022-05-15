package de.cuuky.cfw;

import de.cuuky.cfw.configuration.serialization.BasicSerializable;
import de.cuuky.cfw.configuration.serialization.SerializableLocation;
import de.cuuky.cfw.configuration.serialization.Serialize;
import de.cuuky.cfw.version.BukkitVersion;
import de.cuuky.cfw.version.ServerSoftware;
import de.cuuky.cfw.version.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

// TODO: Maybe move to CFW or other package
public class Hologramm extends BasicSerializable {

    @Serialize("location")
    private SerializableLocation location;

    @Serialize("nameTagUuid")
    private UUID nameTagUuid;

    public Hologramm(JavaPlugin plugin, Location location, String name) {
        this.location = new SerializableLocation(location);
        this.initialize(plugin, name);
    }

    private void checkNameTag(String name) {
        Optional<Entity> armorStand = this.findNameTag();
        if (armorStand.isPresent())
            this.updateNameTag(armorStand.get(), name);
        else
            this.createNameTag(name);
    }

    private Optional<Entity> findNameTag() {
        if (this.location == null || this.nameTagUuid == null
            || VersionUtils.getVersion().isLowerThan(BukkitVersion.ONE_8))
            return Optional.empty();

        return Arrays.stream(location.getChunk().getEntities()).
            filter(entity -> this.nameTagUuid.equals(entity.getUniqueId())).findAny();
    }

    private void createNameTag(String name) {
        Entity armorStand = this.location.getWorld().spawnEntity(this.location, EntityType.ARMOR_STAND);
        this.nameTagUuid = armorStand.getUniqueId();
        this.updateNameTag(armorStand, name);
    }

    private void updateNameTag(Entity armorStand, String name) {
        VersionUtils.getVersionAdapter().setArmorstandAttributes(armorStand, false, true, false, name);
    }

    public void initialize(JavaPlugin plugin, String name) {
        if (VersionUtils.getServerSoftware() == ServerSoftware.PAPER && VersionUtils.getVersion().isHigherThan(
            BukkitVersion.ONE_16))
            // temporary paper 1.17+ workaround
            try {
                Method forceLoadMethod = Chunk.class.getMethod("setForceLoaded", boolean.class);
                forceLoadMethod.invoke(this.location.getChunk(), true);

                this.location.getChunk().load();
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    this.checkNameTag(name);
                    try {
                        forceLoadMethod.invoke(this.location.getChunk(), false);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }, 5L * 20L);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        else {
            this.checkNameTag(name);
        }
    }

    public void updateNameTag(String name) {
        this.findNameTag().ifPresent(e -> this.updateNameTag(e, name));
    }

    public void remove() {
        this.findNameTag().ifPresent(Entity::remove);
    }
}
