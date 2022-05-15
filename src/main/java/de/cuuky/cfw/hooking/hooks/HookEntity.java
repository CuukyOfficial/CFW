package de.cuuky.cfw.hooking.hooks;

import org.bukkit.entity.Player;

import de.cuuky.cfw.hooking.HookManager;

public abstract class HookEntity {

	private HookManager manager;
	private final HookEntityType<?> type;
	private final Player player;

	public HookEntity(HookEntityType<?> type, Player player) {
		this.type = type;
		this.player = player;
	}

	protected abstract HookEntity getExisting(HookManager manager, Player player);

	public void setManager(HookManager manager) {
		HookEntity entity = this.getExisting(manager, this.player);
		if (entity != null) entity.unregister();
		this.manager = manager;
	}

	public void unregister() {
		this.manager.unregisterHook(this);
	}

	public HookEntityType<?> getType() {
		return type;
	}

	public Player getPlayer() {
		return this.player;
	}
}