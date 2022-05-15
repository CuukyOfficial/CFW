package de.cuuky.cfw.hooking.hooks;

import de.cuuky.cfw.hooking.hooks.chat.ChatHook;
import de.cuuky.cfw.hooking.hooks.item.ItemHook;

public class HookEntityType<B extends HookEntity> {

	public static HookEntityType<ChatHook> CHAT = new HookEntityType<>(ChatHook.class);
	public static HookEntityType<ItemHook> ITEM = new HookEntityType<>(ItemHook.class);

	private final Class<B> clazz;

	HookEntityType(Class<B> clazz) {
		this.clazz = clazz;
	}

	public Class<B> getTypeClass() {
		return clazz;
	}
}