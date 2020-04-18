package de.cuuky.cfw.hooking.hooks.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.hooking.hooks.HookEntity;
import de.cuuky.cfw.hooking.hooks.HookEntityType;

public class ChatHook extends HookEntity {

	private ChatHookHandler listener;

	public ChatHook(Player player, String message, ChatHookHandler chatHookListener) {
		super(HookEntityType.CHAT, player);

		this.listener = chatHookListener;

		player.sendMessage(message);
	}
	
	@Override
	public void setManager(HookManager manager) {
		if(manager.getHook(HookEntityType.CHAT, player) != null)
			manager.getHook(HookEntityType.CHAT, player).unregister();
		
		super.setManager(manager);
	}

	public boolean run(AsyncPlayerChatEvent event) {
		boolean unregister = listener.onChat(event);

		if(unregister)
			this.unregister();

		return unregister;
	}
}