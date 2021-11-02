package de.cuuky.cfw.version;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

class OneSevenVersionAdapter implements VersionAdapter {

	protected Class<?> nmsPlayerClass;
	private Method entityHandleMethod;
	protected Method sendPacketMethod;
	protected Field connectionField, networkManagerField, pingField, localeField, xpCooldownField;

	OneSevenVersionAdapter() {
		try {
			this.init();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | ClassNotFoundException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	protected void init() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NoSuchFieldException, ClassNotFoundException {
		this.initEntity();
		this.initPlayer();
		this.initNetworkManager();
		this.initLocale();
		this.initXp();
	}

	protected void initEntity() throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		this.entityHandleMethod = Class
				.forName("org.bukkit.craftbukkit." + VersionUtils.getNmsVersion() + ".entity.CraftEntity")
				.getMethod("getHandle");
	}

	protected void initPlayer()
			throws NoSuchMethodException, SecurityException, NoSuchFieldException, ClassNotFoundException {
		this.nmsPlayerClass = Class.forName(VersionUtils.getNmsClass() + ".EntityPlayer");
		this.pingField = this.nmsPlayerClass.getField("ping");
		this.connectionField = this.nmsPlayerClass.getField("playerConnection");
		this.sendPacketMethod = this.connectionField.getType().getMethod("sendPacket",
				Class.forName(VersionUtils.getNmsClass() + ".Packet"));
	}

	protected void initNetworkManager() throws IllegalArgumentException, IllegalAccessException {
		for (Field field : this.connectionField.getType().getFields())
			if (field.getName().equals("networkManager")) {
				this.networkManagerField = field;
				return;
			}
		throw new Error(
				"[CFW] Failed to initalize 1.7+ networkManager! Are you using a modified version of Spigot/Bukkit?");
	}

	protected void initLocale()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		this.localeField = this.nmsPlayerClass.getDeclaredField("locale");
		this.localeField.setAccessible(true);
	}

	protected void initXp() {
		this.initXp(VersionUtils.getNmsClass() + ".EntityHuman", VersionUtils.getNmsClass() + ".FoodMetaData");
	}

	protected void initXp(String entityHumanName, String foodMetaName) {
		// this is EXTREMELY unsafe
		try {
			int fieldNum = 0;
			for (Field field : Class.forName(entityHumanName).getDeclaredFields())
				if (fieldNum == 0 && field.getType() == Class.forName(foodMetaName))
					fieldNum = 1;
				else if (fieldNum == 1 && field.getType() == int.class)
					fieldNum = 2;
				else if (fieldNum == 2 && field.getType() == float.class)
					fieldNum = 3;
				else if (fieldNum == 3 && field.getType() == float.class)
					fieldNum = 4;
				else if (fieldNum == 4 && field.getType() == int.class) {
					this.xpCooldownField = field;
					return;
				} else
					fieldNum = 0;

			throw new Error("Unable to find xp cooldown field");
		} catch (SecurityException | ClassNotFoundException e) {
			throw new Error(e);
		}
	}

	protected Object getHandle(Entity entity)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return this.entityHandleMethod.invoke(entity);
	}

	@Override
	public Collection<? extends Player> getOnlinePlayers() {
		Object players = Bukkit.getOnlinePlayers();
		return Arrays.asList((Player[]) players);
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
			return this.pingField.getInt(this.getHandle(player));
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public void respawnPlayer(Player player) {
		try {
			Object respawnEnum = Class.forName(VersionUtils.getNmsClass() + ".EnumClientCommand").getEnumConstants()[0];
			Constructor<?>[] constructors = Class.forName(VersionUtils.getNmsClass() + ".PacketPlayInClientCommand")
					.getConstructors();
			for (Constructor<?> constructor : constructors) {
				Class<?>[] args = constructor.getParameterTypes();
				if (args.length == 1 && args[0] == respawnEnum.getClass()) {
					Object packet = Class.forName(VersionUtils.getNmsClass() + ".PacketPlayInClientCommand")
							.getConstructor(args).newInstance(respawnEnum);
					this.sendPacket(player, packet);
					break;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendActionbar(Player player, String message, int duration, Plugin instance) {
		// 1.8+
	}

	@Override
	public void sendActionbar(Player player, String message) {
		// 1.8+
	}

	@Override
	public void sendLinkedMessage(Player player, String message, String link) {
		// 1.8+
		player.sendMessage(message);
	}

	@Override
	public void sendPacket(Player player, Object packet) {
		try {
			this.sendPacketMethod.invoke(this.getConnection(player), packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendTablist(Player player, String header, String footer) {
		// 1.8+
	}

	@Override
	public void sendTitle(Player player, String title, String subtitle) {
		// 1.8+
	}

	@Override
	public void setAttributeSpeed(Player player, double value) {
		// 1.9+
	}

	@Override
	public void setNametagVisibility(Team team, boolean shown) {
		// 1.8+
	}

	@Override
	public void setArmorstandAttributes(Entity armorstand, boolean visible, boolean customNameVisible, boolean gravity,
			String customName) {
		// 1.8+
	}

	@Override
	public void removeAi(LivingEntity entity) {
		entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255));
	}

	@Override
	public void setXpCooldown(Player player, int cooldown) {
		try {
			this.xpCooldownField.set(this.getHandle(player), cooldown);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteItemAnnotations(ItemStack item) {
		// 1.8+ (?)
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

	@Override
	public void forceClearWorlds() {
		// 1.16+
	}

	@Override
	public Properties getServerProperties() {
		try {
			Class<?> mcServerClass = Class.forName(VersionUtils.getNmsClass() + ".MinecraftServer");
			Object mcServer = mcServerClass.getMethod("getServer").invoke(null);
			Field propertyManagerField = mcServer.getClass().getField("propertyManager");
			propertyManagerField.setAccessible(true);
			Object propertyManager = propertyManagerField.get(mcServer);
			Field propertyField = propertyManager.getClass().getDeclaredField("properties");
			propertyField.setAccessible(true);

			return (Properties) propertyField.get(propertyManager);
		} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException
				| SecurityException | InvocationTargetException | NoSuchMethodException e) {
			throw new Error(e);
		}
	}

	@Override
	public boolean supportsAntiXray() {
		return false;
	}

	@Override
	public void setAntiXrayEnabled(boolean enabled) {
		throw new UnsupportedOperationException();
	}
}
