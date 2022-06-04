/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cuuky.cfw.version;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.cuuky.cfw.version.minecraft.MinecraftVersion;
import de.cuuky.cfw.version.minecraft.utils.ProtocolSupportUtils;
import de.cuuky.cfw.version.minecraft.utils.ViaVersionUtils;

public class VersionUtils {

    private static final String FORGE_CLASS = "net.minecraftforge.common.MinecraftForge";

    private static final String nmsClass;
    private static final String nmsVersion;
    private static final boolean forgeSupport;
    @Deprecated()
    private static Object spigot;

    private final static BukkitVersion version;
    private final static ServerSoftware serverSoftware;
    private final static VersionAdapter versionAdapter;
    private final static Map<Player, MinecraftVersion> playerVersions;

    static {
        forgeSupport = isClassPresent(FORGE_CLASS);
        playerVersions = new HashMap<>();

        if (Bukkit.getServer() == null) {
            version = BukkitVersion.UNSUPPORTED;
            serverSoftware = ServerSoftware.UNKNOWN;
            nmsClass = null;
            nmsVersion = null;
        } else {

            String base = "net.minecraft";
            nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            if (nmsVersion.startsWith("v1")) {
                // 1.7 - 1.16
                nmsClass = base + ".server." + nmsVersion;
            } else {
                // Thermos (1.17+ does not use this string at all)
                nmsClass = base + ".server";
            }
            version = BukkitVersion.getVersion(nmsVersion);
            serverSoftware = ServerSoftware.getServerSoftware();

            try {
                spigot = Bukkit.getServer().getClass().getDeclaredMethod("spigot").invoke(Bukkit.getServer());
            } catch (Exception e) {
            }
        }
        versionAdapter = serverSoftware.getVersionAdapter(version.getAdapterSupplier());
    }

    /**
     * @param clazz Class you want to check
     * @return Whether the provided class is loaded
     */
    static boolean isClassPresent(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Use {@link VersionAdapter#setServerProperty} instead
     * 
     * @param key   The key
     * @param value The new value
     * @deprecated Use {@link VersionAdapter#setServerProperty} instead
     */
    @Deprecated
    public static void setMinecraftServerProperty(String key, Object value) {
        versionAdapter.setServerProperty(key, value);
    }

    @Deprecated
    public static double getHearts(Player player) {
        return player.getHealth();
    }

    static String getNmsClass() {
        return nmsClass;
    }

    public static String getNmsVersion() {
        return nmsVersion;
    }

    /**
     * @return Whether the software has support for Forge mods
     */
    public static boolean hasForgeSupport() {
        return forgeSupport;
    }

    /**
     * Use {@link VersionAdapter} instead
     * 
     * @return The spigot object
     */
    @Deprecated()
    public static Object getSpigot() {
        return spigot;
    }

    public static MinecraftVersion getMinecraftVersion(Player player) {
        MinecraftVersion version = playerVersions.get(player);
        if (version != null)
            return version;

        int protocolId = -1;
        if (ViaVersionUtils.isAvailable())
            protocolId = ViaVersionUtils.getVersion(player);
        else if (ProtocolSupportUtils.isAvailable())
            protocolId = ProtocolSupportUtils.getVersion(player);
        else
            System.err.println("[CFW] Cannot get version of player without protocolsupport or viaversion installed");

        playerVersions.put(player, version = MinecraftVersion.getMinecraftVersion(protocolId));
        return version;
    }

    public static BukkitVersion getVersion() {
        return version;
    }

    public static VersionAdapter getVersionAdapter() {
        return versionAdapter;
    }

    public static ServerSoftware getServerSoftware() {
        return serverSoftware;
    }
}
