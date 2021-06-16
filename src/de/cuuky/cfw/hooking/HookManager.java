package de.cuuky.cfw.hooking;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.hooking.hooks.HookEntity;
import de.cuuky.cfw.hooking.hooks.HookEntityType;
import de.cuuky.cfw.hooking.hooks.item.ItemHook;
import de.cuuky.cfw.hooking.listener.HookListener;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;

public class HookManager extends FrameworkManager {

	private List<HookEntity> hooks;

	public HookManager(JavaPlugin instance) {
		super(FrameworkManagerType.HOOKING, instance);

		this.hooks = new ArrayList<>();
		this.ownerInstance.getServer().getPluginManager().registerEvents(new HookListener(this), ownerInstance);
	}

	public <B extends HookEntity> B registerHook(B hook) {
		hook.setManager(this);
		hooks.add(hook);
		return hook;
	}

	public boolean unregisterHook(HookEntity hook) {
		return hooks.remove(hook);
	}

	public void clearHooks() {
		for (int i = hooks.size() - 1; i > -1; i--)
			hooks.get(i).unregister();
	}

	public void clearHooks(HookEntityType type) {
		for (int i = hooks.size() - 1; i > -1; i--) {
			HookEntity ent = hooks.get(i);
			if (ent.getType() != type)
				continue;

			hooks.get(i).unregister();
		}
	}

	public List<HookEntity> getHooks(HookEntityType type) {
		return (List<HookEntity>) getHooks(type.getTypeClass());
	}

	public <B extends HookEntity> List<B> getHooks(Class<B> clazz) {
		ArrayList<B> rHooks = new ArrayList<>();
		for (HookEntity ent : hooks)
			if (ent.getType().getTypeClass().equals(clazz))
				rHooks.add((B) ent);

		return rHooks;
	}

	public HookEntity getHook(HookEntityType type, Player player) {
		return getHook(type.getTypeClass(), player);
	}

	public <B extends HookEntity> B getHook(Class<B> clazz, Player player) {
		for (B ent : getHooks(clazz))
			if (ent.getPlayer().equals(player))
				return (B) ent;

		return null;
	}

	public ItemHook getItemHook(ItemStack stack, Player player) {
		for (HookEntity ent : hooks)
			if (ent.getType() == HookEntityType.ITEM && ent.getPlayer().equals(player) && ((ItemHook) ent).getItemStack().equals(stack))
				return (ItemHook) ent;

		return null;
	}

	public List<HookEntity> getHooks() {
		return hooks;
	}
}