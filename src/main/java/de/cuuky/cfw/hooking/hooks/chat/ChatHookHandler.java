package de.cuuky.cfw.hooking.hooks.chat;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

public interface ChatHookHandler {

	/*
	 * @return Return if the hooks should be removed
	 */
	default boolean onChat(AsyncPlayerChatEvent event) {
		return false;
	}

	@Deprecated
	/*
	 * @return Return if the hooks should be removed
	 */
	default boolean onChat(PlayerChatEvent event) {
		return false;
	}
}