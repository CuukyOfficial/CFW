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

package de.varoplugin.cfw.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class PlayerProfileUtils {

    private static final int TIMEOUT_DEFAULT_MILLIS = 30_000;

    private PlayerProfileUtils() {
        // nop
    }

    /**
     * Blocks the current thread and sends a request to Mojang's API in order to fetch the {@link UUID} that belongs to the player with the given name.
     * 
     * @param name    The name of the player.
     * @param timeout The connection timeout in milliseconds. Should be &gt;= 0.
     * @return A {@link PlayerLookup} element containing the name and uuid.
     * @throws IllegalArgumentException If the name is {@code null} or if timeout is negative.
     */
    public static PlayerLookup fetchByName(String name, int timeout) {
        if (name == null || timeout < 0)
            throw new IllegalArgumentException();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);

            if (connection.getResponseCode() == 204)
                return new PlayerLookup(Result.UNKNOWN_PLAYER, null, null, null);

            try (Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(reader);
                return new PlayerLookup(Result.SUCCESS, stringToUuid(jsonObject.get("id").toString()), (String) jsonObject.get("name"), null);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return new PlayerLookup(Result.ERROR, null, null, t);
        }
    }

    /**
     * Blocks the current thread and sends a request to Mojang's API in order to fetch the {@link UUID} that belongs to the player with the given name.
     * 
     * @param name The name of the player.
     * @return A {@link PlayerLookup} element containing the name and uuid.
     * @throws IllegalArgumentException If the name is {@code null}.
     */
    public static PlayerLookup fetchByName(String name) {
        return fetchByName(name, TIMEOUT_DEFAULT_MILLIS);
    }

    /**
     * Blocks the current thread and sends a request to Mojang's API in order to fetch the name that belongs to the player with the given {@link UUID}.
     * 
     * @param uuid    The uuid of the player.
     * @param timeout The connection timeout in milliseconds. Should be &gt;=0.
     * @return A {@link PlayerLookup} element containing the name and uuid.
     * @throws IllegalArgumentException If the uuid is {@code null} or if timeout is negative.
     */
    public static PlayerLookup fetchByUuid(UUID uuid, int timeout) {
        if (uuid == null || timeout < 0)
            throw new IllegalArgumentException();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidToString(uuid)).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);

            if (connection.getResponseCode() == 400)
                return new PlayerLookup(Result.UNKNOWN_PLAYER, null, null, null);

            try (Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(reader);
                return new PlayerLookup(Result.SUCCESS, uuid, (String) jsonObject.get("name"), null);
            }
        } catch (Throwable t) {
            return new PlayerLookup(Result.ERROR, null, null, t);
        }
    }

    /**
     * Blocks the current thread and sends a request to Mojang's API in order to fetch the name that belongs to the player with the given {@link UUID}.
     * 
     * @param uuid The uuid of the player.
     * @return A {@link PlayerLookup} element containing the name and uuid.
     * @throws IllegalArgumentException If the uuid is {@code null}.
     */
    public static PlayerLookup fetchByUuid(UUID uuid) {
        return fetchByUuid(uuid, TIMEOUT_DEFAULT_MILLIS);
    }

    /**
     * If the player with the given name is online, this returns a {@link PlayerLookup} object containing the player's name and {@link UUID}.
     * 
     * @param name The name of the player.
     * @return A {@link PlayerLookup} element containing the name and uuid.
     * @throws IllegalArgumentException If the name is {@code null}.
     */
    public static PlayerLookup getByName(String name) {
        if (name == null)
            throw new IllegalArgumentException();

        Player player = Bukkit.getPlayer(name);
        return player == null ? new PlayerLookup(Result.UNKNOWN_PLAYER, null, null, null) : new PlayerLookup(Result.SUCCESS, player.getUniqueId(), player.getName(), null);
    }

    /**
     * If the player with the given uuid is online, this returns a {@link PlayerLookup} object containing the player's name and {@link UUID}.
     * 
     * @param uuid The uuid of the player.
     * @return A {@link PlayerLookup} element containing the name and uuid.
     * @throws IllegalArgumentException If the uuid is {@code null}.
     */
    public static PlayerLookup getByUuid(UUID uuid) {
        if (uuid == null)
            throw new IllegalArgumentException();

        Player player = Bukkit.getPlayer(uuid);
        return player == null ? new PlayerLookup(Result.UNKNOWN_PLAYER, null, null, null) : new PlayerLookup(Result.SUCCESS, player.getUniqueId(), player.getName(), null);
    }

    /**
     * If the player with the given name is online, this immediately returns a {@link PlayerLookup} object containing the player's name and {@link UUID}.
     * Otherwise this blocks the current thread and sends a request to Mojang's API in order to fetch the {@link UUID} that belongs to the player with the
     * given name.
     * 
     * @param name    The name of the player.
     * @param timeout The connection timeout in milliseconds. Should be &gt;= 0.
     * @return A {@link PlayerLookup} element containing the name and uuid.
     * @throws IllegalArgumentException If the name is {@code null} or if timeout is negative.
     */
    public static PlayerLookup getOrFetchByName(String name, int timeout) {
        if (name == null || timeout < 0)
            throw new IllegalArgumentException();

        PlayerLookup player = getByName(name);
        return player.getResult() == Result.SUCCESS ? player : fetchByName(name, timeout);
    }

    /**
     * If the player with the given name is online, this immediately returns a {@link PlayerLookup} object containing the player's name and {@link UUID}.
     * Otherwise this blocks the current thread and sends a request to Mojang's API in order to fetch the {@link UUID} that belongs to the player with the
     * given name.
     * 
     * @param name The name of the player.
     * @return A {@link PlayerLookup} element containing the name and uuid.
     * @throws IllegalArgumentException If the name is {@code null}.
     */
    public static PlayerLookup getOrFetchByName(String name) {
        return getOrFetchByName(name, TIMEOUT_DEFAULT_MILLIS);
    }

    /**
     * If the player with the given uuid is online, this immediately returns a {@link PlayerLookup} object containing the player's name and {@link UUID}.
     * Otherwise this blocks the current thread and sends a request to Mojang's API in order to fetch the name that belongs to the player with the given
     * {@link UUID}.
     * 
     * @param uuid    The uuid of the player.
     * @param timeout The connection timeout in milliseconds. Should be &gt;= 0.
     * @return A {@link PlayerLookup} element containing the name and uuid.
     * @throws IllegalArgumentException If the name is {@code null} or if timeout is negative.
     */
    public static PlayerLookup getOrFetchByUuid(UUID uuid, int timeout) {
        if (uuid == null || timeout < 0)
            throw new IllegalArgumentException();

        PlayerLookup player = getByUuid(uuid);
        return player.getResult() == Result.SUCCESS ? player : fetchByUuid(uuid, timeout);
    }

    /**
     * If the player with the given uuid is online, this immediately returns a {@link PlayerLookup} object containing the player's name and {@link UUID}.
     * Otherwise this blocks the current thread and sends a request to Mojang's API in order to fetch the name that belongs to the player with the given
     * {@link UUID}.
     * 
     * @param uuid The uuid of the player.
     * @return A {@link PlayerLookup} element containing the name and uuid.
     * @throws IllegalArgumentException If the name is {@code null}.
     */
    public static PlayerLookup getOrFetchByUuid(UUID uuid) {
        return getOrFetchByUuid(uuid, TIMEOUT_DEFAULT_MILLIS);
    }

    /**
     * Returns a {@link PlayerLookup} object containing the offline uuid of the player with a given name. This does not send any web requests.
     * 
     * @param name The name of the player.
     * @return A {@link PlayerLookup} object containing the name and uuid of the player.
     * @throws IllegalArgumentException If the name is {@code null}.
     */
    public static PlayerLookup getCrackedByName(String name) {
        if (name == null)
            throw new IllegalArgumentException();

        UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
        return new PlayerLookup(Result.SUCCESS, offlineUUID, name, null);
    }

    private static UUID stringToUuid(String string) {
        if (string.length() != 32)
            throw new IllegalArgumentException();
        return UUID.fromString(string.substring(0, 8) + "-" + string.substring(8, 12) + "-" + string.substring(12, 16) + "-" + string.substring(16, 20) + "-" + string.substring(20, 32));
    }

    private static String uuidToString(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    public static class PlayerLookup {

        private final Result result;
        private final UUID uuid;
        private final String name;
        private final Throwable exception;

        PlayerLookup(Result result, UUID uuid, String name, Throwable exception) {
            if (result == null)
                throw new IllegalArgumentException();

            this.result = result;
            this.uuid = uuid;
            this.name = name;
            this.exception = exception;
        }

        /**
         * Returns the {@link Result}
         * 
         * @return The result
         */
        public Result getResult() {
            return this.result;
        }

        /**
         * Returns the {@link UUID} of the player. May be {@code null} if {@link #getResult()} != {@link Result#SUCCESS}.
         * 
         * @return The UUID
         */
        public UUID getUuid() {
            return this.uuid;
        }

        /**
         * Returns the name of the player. May be {@code null} if {@link #getResult()} != {@link Result#SUCCESS}.
         * 
         * @return The name
         */
        public String getName() {
            return this.name;
        }

        /**
         * If the lookup failed, this may return an exception. If {@link #getResult()} != {@link Result#ERROR} this will be {@code null}.
         * 
         * @return The exception
         */
        public Throwable getException() {
            return this.exception;
        }

        /**
         * Returns the {@link Player}, if they are online and the lookup succeeded.
         * 
         * @return The player
         */
        public Player getPlayer() {
            return this.result == Result.SUCCESS ? Bukkit.getPlayer(this.uuid) : null;
        }

        @Override
        public String toString() {
            return "PlayerLookup [result=" + this.result + ", uuid=" + this.uuid + ", name=" + this.name + ", exception=" + this.exception + "]";
        }
    }

    public static enum Result {
        SUCCESS,
        UNKNOWN_PLAYER,
        ERROR;
    }
}
