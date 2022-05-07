package de.cuuky.cfw.utils;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ShapedRecipe;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public final class UUIDUtils {

	private static UUID getUUIDTime(String name, long time, int timeout) throws Exception {
		URL url;
		if (time == -1) {
			url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
		} else {
			//the timestamp parameter is no longer supported by mojang https://wiki.vg/Mojang_API#Username_to_UUID
			url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name + "?at=" + time);
		}
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(timeout);
		connection.setReadTimeout(timeout);

		if(connection.getResponseCode() == 204) {
			//unknown name
			return null;
		}

		Scanner scanner = new Scanner(connection.getInputStream());

		String input = scanner.nextLine();
		scanner.close();

		JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(input);
		String uuidString = UUIDObject.get("id").toString();
		String uuidSeperation = uuidString.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
        return UUID.fromString(uuidSeperation);
	}

	private static UUID getUUIDTime(String name, long time) throws Exception {
		return getUUIDTime(name, time, 30000);
	}

    @Deprecated
    public static String getNamesChanged(String name) throws Exception {
        return getName(name);
    }

    public static String getName(UUID uuid) throws Exception {
        String uuidString = uuid.toString().replace("-", "");
        Scanner scanner = new Scanner(new URL("https://api.mojang.com/user/profiles/" + uuidString + "/names").openStream());
        String input = scanner.nextLine();
        scanner.close();

        JSONArray nameArray = (JSONArray) JSONValue.parseWithException(input);
        String playerSlot = nameArray.get(nameArray.size() - 1).toString();
        JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
        return nameObject.get("name").toString();
    }

	public static String getName(String name) throws Exception {
		Date Date = new Date();
		long Time = Date.getTime() / 1000;

		UUID UUIDOld = getUUIDTime(name, (Time - (60 * 60 * 24 * 30)));
		return getName(UUIDOld);
	}

	public static UUID getCrackedUUID(String name) throws UnsupportedEncodingException {
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
	}

	public static UUID getUUID(String name) throws Exception {
		return getUUIDTime(name, -1);
	}

	public static UUID getUUID(String name, int timeout) throws Exception {
		return getUUIDTime(name, -1, timeout);
	}
}