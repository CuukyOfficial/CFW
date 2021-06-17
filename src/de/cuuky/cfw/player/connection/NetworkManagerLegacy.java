package de.cuuky.cfw.player.connection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.cuuky.cfw.version.BukkitVersion;
import de.cuuky.cfw.version.VersionUtils;

public class NetworkManagerLegacy {

	// I know this is made very badly, but hey, it works

	// CHAT TITLE
	private static Class<?> chatMessageTypeClass;

	private static Object genericSpeedType;
	private static Class<?> ioBase;

	// IOBASE
	private static Class<?> ioBaseChat;

	// private static Class<?> titleClass;
	private static Constructor<?> titleConstructor;
	private static Method sendTitleMethod;
	private static Class<?> packetChatClass;

	// TAB
	private static Class<?> tablistClass;
	private static Method ioBaseChatMethod;

	// ACTIONBAR
	private static Object title, subtitle;
	private static Constructor<?> chatByteMethod, chatEnumMethod, chatEnumUUIDMethod;

	static {
		try {
			//ioBaseChat = VersionUtils.getChatSerializer();

			try {
				ioBase = Class.forName(VersionUtils.getNmsClass() + ".IChatBaseComponent");
			} catch (ClassNotFoundException e) {
				ioBase = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
			}
			ioBaseChatMethod = ioBaseChat.getDeclaredMethod("a", String.class);

			String path = VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_16) ? VersionUtils.getNmsClass() + ".network.protocol.game" : VersionUtils.getNmsClass();
			if (VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_7)) {
				if(VersionUtils.getVersion().isLowerThan(BukkitVersion.ONE_17)) {
					Class<?> titleClass = Class.forName(path + ".PacketPlayOutTitle");

					Class<?> enumTitleClass = null;
					try {
						enumTitleClass = titleClass.getDeclaredClasses()[0];
					} catch (Exception e) {
						enumTitleClass = Class.forName(path + ".EnumTitleAction");
					}

					title = enumTitleClass.getDeclaredField("TITLE").get(null);
					subtitle = enumTitleClass.getDeclaredField("SUBTITLE").get(null);

					titleConstructor = titleClass.getConstructor(enumTitleClass, ioBase, int.class, int.class, int.class);
				}else
					sendTitleMethod = Player.class.getDeclaredMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);

				packetChatClass = Class.forName(path + ".PacketPlayOutChat");
				try {
					chatMessageTypeClass = Class.forName(path + ".ChatMessageType");
					try {
						chatEnumMethod = packetChatClass.getConstructor(ioBase, chatMessageTypeClass);
					} catch (Exception e) {
						chatEnumUUIDMethod = packetChatClass.getConstructor(ioBase, chatMessageTypeClass, UUID.class);
					}
				} catch (Exception e) {
					chatByteMethod = packetChatClass.getConstructor(ioBase, byte.class);
				}

				tablistClass = Class.forName(path + ".PacketPlayOutPlayerListHeaderFooter");
			}
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	private Player player;
	private Object connection, playerHandle, tablist, networkManager;
	private Method sendPacketMethod;

	private Field footerField, headerField;
	private Field pingField;

	private String locale;

	public NetworkManagerLegacy(Player player) {
		this.player = player;

		try {
			this.playerHandle = player.getClass().getMethod("getHandle").invoke(player);

			if(VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_16)) {
				this.pingField = playerHandle.getClass().getField("e");
				this.connection = playerHandle.getClass().getField("b").get(playerHandle);
				this.sendPacketMethod = connection.getClass().getMethod("sendPacket", Class.forName(VersionUtils.getNmsClass() + ".network.protocol.Packet"));
			} else {
				this.pingField = playerHandle.getClass().getField("ping");
				this.connection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
				this.sendPacketMethod = connection.getClass().getMethod("sendPacket", Class.forName(VersionUtils.getNmsClass() + ".Packet"));
			}

			for (Field f : this.connection.getClass().getFields())
				if (f.getName().equals("networkManager"))
					this.networkManager = f.get(this.connection);

			if (this.networkManager == null)
				System.err.println("[CFW] Failed to initalize networkManager! Are you using a modified version of Spigot/Bukkit?");
			else {
				if (VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_11)) {
					Method localeMethod = player.getClass().getDeclaredMethod("getLocale");
					localeMethod.setAccessible(true);
					this.locale = (String) localeMethod.invoke(player);
				} else {
					try {
						Field localeField = playerHandle.getClass().getField("locale");
						localeField.setAccessible(true);
						this.locale = (String) localeField.get(playerHandle);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				this.locale = this.locale.toLowerCase();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getConnection() {
		return connection;
	}

	public int getPing() {
		try {
			return pingField.getInt(playerHandle);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	public Player getPlayer() {
		return player;
	}

	public void respawnPlayer() {
		
	}

	public void sendActionbar(String message, int duration, Plugin instance) {
		if (!VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_7))
			return;

		new BukkitRunnable() {

			private int count;

			@Override
			public void run() {
				sendActionbar(message);

				if (count >= duration)
					this.cancel();

				count++;
			}
		}.runTaskTimerAsynchronously(instance, 0, 20);
	}

	public void sendActionbar(String message) {
		if (!VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_7))
			return;

		try {
			Object barchat = ioBaseChatMethod.invoke(ioBaseChat, "{\"text\": \"" + message + "\"}");

			Object packet = null;
			if (chatByteMethod != null)
				packet = chatByteMethod.newInstance(barchat, (byte) 2);
			else if (chatEnumMethod != null)
				packet = chatEnumMethod.newInstance(barchat, chatMessageTypeClass.getDeclaredField("GAME_INFO").get(null));
			else
				packet = chatEnumUUIDMethod.newInstance(barchat, chatMessageTypeClass.getDeclaredField("GAME_INFO").get(null), this.player.getUniqueId());

			sendPacket(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendLinkedMessage(String message, String link) {
		try {
			Object text = ioBaseChatMethod.invoke(ioBaseChat, "{\"text\": \"" + message + "\", \"color\": \"white\", \"clickEvent\": {\"action\": \"open_url\" , \"value\": \"" + link + "\"}}");
			Object packetFinal = null;
			try {
				Constructor<?> constructor = packetChatClass.getConstructor(ioBase);
				packetFinal = constructor.newInstance(text);
			} catch (Exception e) {
				Constructor<?> constructor = packetChatClass.getConstructor(ioBase, chatMessageTypeClass, UUID.class);
				packetFinal = constructor.newInstance(text, chatMessageTypeClass.getDeclaredField("CHAT").get(null), this.player.getUniqueId());
			}

			Field field = packetFinal.getClass().getDeclaredField("a");
			field.setAccessible(true);
			field.set(packetFinal, text);

			sendPacket(packetFinal);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void sendPacket(Object packet) {
		try {
			sendPacketMethod.invoke(connection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendTablist(String header, String footer) {
		if (!VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_7))
			return;

		try {
			Object tabheader = ioBaseChatMethod.invoke(ioBaseChat, "{\"text\": \"" + header + "\"}");
			Object tabfooter = ioBaseChatMethod.invoke(ioBaseChat, "{\"text\": \"" + footer + "\"}");

			if (tablist == null) {
				tablist = tablistClass.newInstance();

				headerField = getField(tablist.getClass(), "a", "header");
				headerField.setAccessible(true);

				footerField = getField(tablist.getClass(), "b", "footer");
				footerField.setAccessible(true);
			}

			headerField.set(tablist, tabheader);
			footerField.set(tablist, tabfooter);

			sendPacket(tablist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendTitle(String header, String footer) {
		if (!VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_7))
			return;

		try {
			if(VersionUtils.getVersion().isLowerThan(BukkitVersion.ONE_17)) {
				Object titleHeader = ioBaseChatMethod.invoke(ioBaseChat, "{\"text\": \"" + header + "\"}");
				Object titleFooter = ioBaseChatMethod.invoke(ioBaseChat, "{\"text\": \"" + footer + "\"}");

				Object headerPacket = titleConstructor.newInstance(title, titleHeader, 0, 2, 0);
				Object footerPacket = titleConstructor.newInstance(subtitle, titleFooter, 0, 2, 0);

				sendPacket(headerPacket);
				sendPacket(footerPacket);
			}else {
				sendTitleMethod.invoke(this.player, header, footer, 0, 2, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setAttributeSpeed(double value) {
		if (!VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_8))
			return;

		try {
			if (genericSpeedType == null) {
				Class<?> attribute = Class.forName("org.bukkit.attribute.Attribute");
				genericSpeedType = attribute.getField("GENERIC_ATTACK_SPEED").get(attribute);
			}

			Object attributeInstance = player.getClass().getMethod("getAttribute", genericSpeedType.getClass()).invoke(player, genericSpeedType);

			attributeInstance.getClass().getMethod("setBaseValue", double.class).invoke(attributeInstance, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getNetworkManager() {
		return networkManager;
	}

	public String getLocale() {
		return locale;
	}

	private static Field getField(Class<?> clazz, String... strings) {
		for (String s : strings) {
			try {
				return clazz.getDeclaredField(s);
			} catch (NoSuchFieldException e) {
				continue;
			}
		}

		return null;
	}
}