package de.cuuky.cfw.version;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class OneThirteenVersionAdapter extends OneTwelveVersionAdapter {

	@Override
	protected void initTablist()
			throws NoSuchFieldException, SecurityException, ClassNotFoundException, NoSuchMethodException {
	}
	
	@Override
	protected void initChat() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
	}

	@Override
	protected void initNetworkManager() throws IllegalArgumentException, IllegalAccessException {
	}
	
	@Override
	public void sendActionbar(Player player, String message) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
	}

    @Override
    public void sendClickableMessage(Player player, String message, Action action, String value) {
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(message));
        component.setClickEvent(new ClickEvent(action, value));
        player.spigot().sendMessage(ChatMessageType.CHAT, component);
    }
	
	@Override
	public void sendTablist(Player player, String header, String footer) {
		player.setPlayerListHeaderFooter(header, footer);
	}
}
