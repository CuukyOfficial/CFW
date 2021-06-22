package de.cuuky.cfw.version;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OneSixteenVersionAdapter extends OneFourteenVersionAdapter {

	@Override
	protected void initPacketChatArgConstructor() throws NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		Class<?> messageTypeClass = Class.forName(VersionUtils.getNmsClass() + ".ChatMessageType");
		this.messageTypeSystem = messageTypeClass.getDeclaredField("SYSTEM").get(null);
		this.messageTypeGameInfo = messageTypeClass.getDeclaredField("GAME_INFO").get(null);
		this.packetChatConstructor = this.packetChatClass.getConstructor(this.chatBaseComponentInterface, messageTypeClass, UUID.class);
	}

	@Override
	protected Object getActionbarPacket(Player player, Object text) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return this.packetChatConstructor.newInstance(text, this.messageTypeGameInfo, player.getUniqueId());
	}

	@Override
	protected Object getMessagePacket(Player player, Object text) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return this.packetChatConstructor.newInstance(text, this.messageTypeSystem, player.getUniqueId());
	}

	@Override
	public void forceClearWorlds() {
		try {
			Field dedicatedServerField = Class.forName("org.bukkit.craftbukkit." + VersionUtils.getNmsVersion() + ".CraftServer").getDeclaredField("console");
			dedicatedServerField.setAccessible(true);
			Object dedicatedServer = dedicatedServerField.get(Bukkit.getServer());
			Map<?, ?> worldServer = (Map<?, ?>) dedicatedServer.getClass().getField(this.getWorldServerFieldName()).get(dedicatedServer);
			worldServer.clear();
		}catch(ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new Error(e);
		}
	}
	
	protected String getWorldServerFieldName() {
		return "worldServer";
	}
}
