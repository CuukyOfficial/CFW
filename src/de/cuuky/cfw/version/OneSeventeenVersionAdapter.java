package de.cuuky.cfw.version;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

class OneSeventeenVersionAdapter extends OneTwelveVersionAdapter {

	private Method tablistMethod;

	private Object messageTypeChat;
	private Object messageTypeGameInfo;

	protected void initPlayer() throws NoSuchMethodException, SecurityException, NoSuchFieldException, ClassNotFoundException {
		this.playerClass = Class.forName("org.bukkit.craftbukkit." + VersionUtils.getNmsVersion() + ".entity.CraftPlayer");
		this.playerHandleMethod = this.playerClass.getMethod("getHandle");

		this.pingField = this.playerHandleMethod.getReturnType().getField("e");
		this.connectionField = this.playerHandleMethod.getReturnType().getField("b");
		this.sendPacketMethod = this.connectionField.getType().getMethod("sendPacket", Class.forName("net.minecraft.network.protocol.Packet"));
	}

	@Override
	protected void initNetworkManager() throws IllegalArgumentException, IllegalAccessException {
		for (Field field : this.connectionField.getType().getFields())
			if (field.getType().getName().equals("net.minecraft.network.NetworkManager")) {
				this.networkManagerField = field;
				return;
			}
		throw new Error("[CFW] Failed to initalize 1.17 networkManager! Are you using a modified version of Spigot/Bukkit?");
	}

	@Override
	protected void initTablist() throws NoSuchFieldException, SecurityException, ClassNotFoundException, NoSuchMethodException {
		this.tablistMethod = Player.class.getDeclaredMethod("setPlayerListHeaderFooter", String.class, String.class);
	}

	@Override
	protected void initChat() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		this.chatBaseComponentInterface = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
		Class<?> chatSerializer = Class.forName("net.minecraft.network.chat.IChatBaseComponent$ChatSerializer");
		this.chatSerializerMethod = chatSerializer.getDeclaredMethod("a", String.class);

		this.packetChatClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutChat");

		Class<?> chatEnumTypeClass = Class.forName("net.minecraft.network.chat.ChatMessageType");
		this.messageTypeChat = chatEnumTypeClass.getDeclaredField("b").get(null); //CHAT
		this.messageTypeGameInfo = chatEnumTypeClass.getDeclaredField("c").get(null); //GAME_INFO

		this.packetChatConstructor = this.packetChatClass.getConstructor(this.chatBaseComponentInterface, chatEnumTypeClass, UUID.class);
	}

	protected void initXp() {
		this.initXp("net.minecraft.world.entity.player.EntityHuman", "net.minecraft.world.food.FoodMetaData");
	}

	@Override
	public void respawnPlayer(Player player) {
		throw new Error("No implemented yet");
	}

	@Override
	protected Object getActionbarPacket(Player player, Object text) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return this.packetChatConstructor.newInstance(text, this.messageTypeGameInfo, player.getUniqueId());
	}

	@Override
	protected Object getMessagePacket(Player player, Object text) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return this.packetChatConstructor.newInstance(text, this.messageTypeChat, player.getUniqueId());
	}

	@Override
	public void sendTablist(Player player, String header, String footer) {
		try {
			this.tablistMethod.invoke(player, header, footer);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Properties getServerProperties() {
		try {
			Method dedicatedServerPropMethod = Bukkit.getServer().getClass().getDeclaredMethod("getProperties");
			dedicatedServerPropMethod.setAccessible(true);
			Object dedicatedServerProp = dedicatedServerPropMethod.invoke(Bukkit.getServer());
			Field[] fields = dedicatedServerProp.getClass().getFields();
			for (Field field : fields) {
				if (!field.getType().equals(Properties.class))
					continue;

				field.setAccessible(true);
				return (Properties) field.get(dedicatedServerProp);
			}
			throw new Error("missing propertys field");
		}catch(NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new Error(e);
		}
	}
}
