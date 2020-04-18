package de.cuuky.cfw.hooking.hooks;

import org.bukkit.entity.Player;

import de.cuuky.cfw.hooking.HookManager;

public abstract class HookEntity {

	protected HookManager manager;
	protected HookEntityType type;
	protected Player player;

	public HookEntity(HookEntityType type, Player player) {
		this.type = type;
		this.player = player;
	}
	
	public void setManager(HookManager manager) {
		this.manager = manager;
	}

	public void unregister() {
		this.manager.unregisterHook(this);
	}
	
	public HookEntityType getType() {
		return type;
	}

	public Player getPlayer() {
		return this.player;
	}
}