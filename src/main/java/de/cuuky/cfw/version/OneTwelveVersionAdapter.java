package de.cuuky.cfw.version;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

class OneTwelveVersionAdapter extends OneNineVersionAdapter {

	private Method localeMethod;
	protected Object messageTypeSystem;
	protected Object messageTypeGameInfo;

	@Override
	protected void initLocale() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try {
			this.localeMethod = Class.forName("org.bukkit.craftbukkit." + VersionUtils.getNmsVersion() + ".entity.CraftPlayer").getDeclaredMethod("getLocale");
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void initPacketChatArgConstructor() throws NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		Class<?> messageTypeClass = Class.forName(VersionUtils.getNmsClass() + ".ChatMessageType");
		this.messageTypeSystem = messageTypeClass.getDeclaredField("SYSTEM").get(null);
		this.messageTypeGameInfo = messageTypeClass.getDeclaredField("GAME_INFO").get(null);
		this.packetChatConstructor = this.packetChatClass.getConstructor(this.chatBaseComponentInterface, messageTypeClass);
	}

	@Override
	protected void initTitle() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, NoSuchMethodException {
	}

	@Override
	protected Object getActionbarPacket(Player player, Object text) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return this.packetChatConstructor.newInstance(text, this.messageTypeGameInfo);
	}

	@Override
	protected Object getMessagePacket(Player player, Object text) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return this.packetChatConstructor.newInstance(text, this.messageTypeSystem);
	}

	@Override
	public void sendTitle(Player player, String title, String subtitle) {
		player.sendTitle(title, subtitle, 10, 70, 20);
	}

	@Override
	public String getLocale(Player player) {
		try {
			return (String) this.localeMethod.invoke(player);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new Error();
		}
	}
}
