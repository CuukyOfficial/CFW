package de.cuuky.cfw.hooking.hooks;

import de.cuuky.cfw.hooking.hooks.chat.ChatHook;
import de.cuuky.cfw.hooking.hooks.item.ItemHook;

public enum HookEntityType {

	CHAT(ChatHook.class),
	ITEM(ItemHook.class);

	private Class<? extends HookEntity> clazz;

	private <B extends HookEntity> HookEntityType(Class<B> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends HookEntity> getTypeClass() {
		return clazz;
	}
}