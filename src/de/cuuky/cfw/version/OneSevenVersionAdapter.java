package de.cuuky.cfw.version;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

class OneSevenVersionAdapter implements VersionAdapter {

	protected Class<?> playerClass;
	protected Method playerHandleMethod, sendPacketMethod;

	protected Field connectionField, networkManagerField, pingField, localeField;

	OneSevenVersionAdapter() {
		try {
			this.init();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	protected void init() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException, ClassNotFoundException {
		this.initPlayer();
		this.initNetworkManager();
		this.initLocale();
	}
	
	protected void initPlayer() throws NoSuchMethodException, SecurityException, NoSuchFieldException, ClassNotFoundException {
		this.playerClass = Class.forName("org.bukkit.craftbukkit." + VersionUtils.getNmsVersion() + ".entity.CraftPlayer");
		this.playerHandleMethod = this.playerClass.getMethod("getHandle");
		this.pingField = this.playerHandleMethod.getReturnType().getField("ping");
		this.connectionField = this.playerHandleMethod.getReturnType().getField("playerConnection");
		this.sendPacketMethod = this.connectionField.getType().getMethod("sendPacket", Class.forName(VersionUtils.getNmsClass() + ".Packet"));
	}

	protected void initNetworkManager() throws IllegalArgumentException, IllegalAccessException {
		for (Field field : this.connectionField.getType().getFields())
			if (field.getName().equals("networkManager")) {
				this.networkManagerField = field;
				return;
			}
		throw new Error("[CFW] Failed to initalize 1.7+ networkManager! Are you using a modified version of Spigot/Bukkit?");
	}

	protected void initLocale() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		this.localeField = this.playerHandleMethod.getReturnType().getDeclaredField("locale");
		this.localeField.setAccessible(true);
	}
	
	private Object getHandle(Player player) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return this.playerHandleMethod.invoke(player);
	}

	@Override
	public Object getConnection(Player player) {
		try {
			return this.connectionField.get(this.getHandle(player));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new Error(e);
		}
	}

	@Override
	public int getPing(Player player) {
		try {
			return pingField.getInt(this.getHandle(player));
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public void respawnPlayer(Player player) {
		try {
			Object respawnEnum = Class.forName(VersionUtils.getNmsClass() + ".EnumClientCommand").getEnumConstants()[0];
			Constructor<?>[] constructors = Class.forName(VersionUtils.getNmsClass() + ".PacketPlayInClientCommand").getConstructors();
			for (Constructor<?> constructor : constructors) {
				Class<?>[] args = constructor.getParameterTypes();
				if (args.length == 1 && args[0] == respawnEnum.getClass()) {
					Object packet = Class.forName(VersionUtils.getNmsClass() + ".PacketPlayInClientCommand").getConstructor(args).newInstance(respawnEnum);
					sendPacket(player, packet);
					break;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendActionbar(Player player, String message, int duration, Plugin instance) {
		//1.8+
	}

	@Override
	public void sendActionbar(Player player, String message) {
		//1.8+
	}

	@Override
	public void sendLinkedMessage(Player player, String message, String link) {
		//1.8+
		player.sendMessage(message);
	}

	@Override
	public void sendPacket(Player player, Object packet) {
		try {
			sendPacketMethod.invoke(this.getConnection(player), packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendTablist(Player player, String header, String footer) {
		//1.8+
	}

	@Override
	public void sendTitle(Player player, String header, String footer) {
		//1.8+
	}

	@Override
	public void setAttributeSpeed(Player player, double value) {
		//1.9+
	}

	@Override
	public Object getNetworkManager(Player player) {
		try {
			return this.networkManagerField.get(this.getConnection(player));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new Error(e);
		}
	}

	@Override
	public String getLocale(Player player) {
		try {
			return ((String) this.localeField.get(this.getHandle(player))).toLowerCase();
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new Error(e);
		}
	}

}
