package de.cuuky.cfw.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.cuuky.cfw.player.CustomPlayer;

public final class PermissionUtils {

	private static Object luckPermsAPI, userManager, groupManager, queryOptions;

	private static HashMap<String, Class<?>> clazzes;

	static {
		clazzes = new HashMap<>();
		ArrayList<Class<?>> toAdd = new ArrayList<>();

		try {
			toAdd.add(Class.forName("ru.tehkode.permissions.bukkit.PermissionsEx"));
		} catch (Exception e) {}

		try {
			toAdd.add(Class.forName("net.luckperms.api.LuckPerms"));
			toAdd.add(Class.forName("net.luckperms.api.query.QueryOptions"));
		} catch (Exception e) {}

		for (Class<?> clazz : toAdd)
			clazzes.put(clazz.getName(), clazz);

		try {
			RegisteredServiceProvider<?> provider = Bukkit.getServicesManager().getRegistration(clazzes.get("net.luckperms.api.LuckPerms"));
			luckPermsAPI = null;

			if (provider != null)
				luckPermsAPI = provider.getProvider();

			userManager = luckPermsAPI.getClass().getMethod("getUserManager").invoke(luckPermsAPI);
			groupManager = luckPermsAPI.getClass().getMethod("getGroupManager").invoke(luckPermsAPI);
			queryOptions = clazzes.get("net.luckperms.api.query.QueryOptions").getDeclaredMethod("defaultContextualOptions").invoke(null);
		} catch (Exception e) {}
	}

	public static String getLuckPermsPrefix(CustomPlayer player) {
		try {
			Object user = userManager.getClass().getMethod("getUser", UUID.class).invoke(userManager, UUID.fromString(player.getUUID()));
			String groupname = (String) user.getClass().getMethod("getPrimaryGroup").invoke(user);

			Object group = groupManager.getClass().getMethod("getGroup", String.class).invoke(groupManager, groupname);
			Object cachedData = group.getClass().getMethod("getCachedData").invoke(group);

			Object metadata = cachedData.getClass().getMethod("getMetaData", clazzes.get("net.luckperms.api.query.QueryOptions")).invoke(cachedData, queryOptions);
			String prefix = (String) metadata.getClass().getMethod("getPrefix").invoke(metadata);
			return prefix != null ? prefix.replace("&", "§") : prefix;
		} catch (Throwable e) {}

		return "";
	}

	public static String getPermissionsExPrefix(CustomPlayer player) {
		String prefix = null;
		try {
			Object permissionUser = clazzes.get("ru.tehkode.permissions.bukkit.PermissionsEx").getDeclaredMethod("getUser", String.class).invoke(null, player.getName());
			Object[] groups = ((Object[]) permissionUser.getClass().getDeclaredMethod("getGroups").invoke(permissionUser));

			if (groups.length > 1)
				prefix = (String) groups[0].getClass().getMethod("getPrefix").invoke(groups[0]);
			else
				prefix = (String) permissionUser.getClass().getMethod("getPrefix").invoke(permissionUser);
		} catch (Throwable e) {}

		return prefix != null ? prefix.replace("&", "§") : prefix;
	}
}