package de.cuuky.cfw.hooking.hooks.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.hooking.hooks.HookEntity;
import de.cuuky.cfw.hooking.hooks.HookEntityType;

import java.util.function.Supplier;

public class ChatHook extends HookEntity {

	private final ChatHookHandler listener;

	public ChatHook(Player player, String message, ChatHookHandler chatHookListener) {
		super(HookEntityType.CHAT, player);

		this.listener = chatHookListener;
		player.sendMessage(message);
	}

	private boolean run(Supplier<Boolean> runner) {
		boolean unregister = runner.get();
		if (unregister) this.unregister();
		return unregister;
	}

	@Override
	protected HookEntity getExisting(HookManager manager, Player player) {
		return manager.getHook(HookEntityType.CHAT, this.getPlayer());
	}

	public boolean run(AsyncPlayerChatEvent event) {
		return this.run(() -> listener.onChat(event));
	}

	public boolean run(PlayerChatEvent event) {
		return this.run(() -> listener.onChat(event));
	}
}