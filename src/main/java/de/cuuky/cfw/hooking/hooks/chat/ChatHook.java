/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cuuky.cfw.hooking.hooks.chat;

import java.util.function.Supplier;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.hooking.hooks.HookEntity;
import de.cuuky.cfw.hooking.hooks.HookEntityType;

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