package de.cuuky.cfw.hooking.hooks.chat;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface ChatHookHandler {

	/*
	 * @return Return if the hooks should be removed
	 */
	public boolean onChat(AsyncPlayerChatEvent event);

}