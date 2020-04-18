package de.cuuky.cfw.hooking;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.cuuky.cfw.CuukyFrameWork;
import de.cuuky.cfw.hooking.hooks.HookEntity;
import de.cuuky.cfw.hooking.hooks.HookEntityType;
import de.cuuky.cfw.hooking.hooks.item.ItemHook;

public class HookManager {

	private CuukyFrameWork instance;
	private ArrayList<HookEntity> hooks;

	public HookManager(CuukyFrameWork instance) {
		this.hooks = new ArrayList<>();
	}

	public HookEntity registerHook(HookEntity hook) {
		hook.setManager(this);
		hooks.add(hook);
		return hook;
	}

	public boolean unregisterHook(HookEntity hook) {
		return hooks.remove(hook);
	}

	public void clearHooks() {
		for(int i = hooks.size() - 1; i > -1; i--)
			hooks.get(i).unregister();
	}

	public void clearHooks(HookEntityType type) {
		for(int i = hooks.size() - 1; i > -1; i--) {
			HookEntity ent = hooks.get(i);
			if(ent.getType() != type)
				continue;

			hooks.get(i).unregister();
		}
	}

	public ArrayList<HookEntity> getHooks(HookEntityType type) {
		ArrayList<HookEntity> rHooks = new ArrayList<>();
		for(HookEntity ent : hooks)
			if(ent.getType() == type)
				rHooks.add(ent);

		return rHooks;
	}

	public HookEntity getHook(HookEntityType type, Player player) {
		for(HookEntity ent : hooks)
			if(ent.getType() == type && ent.getPlayer().equals(player))
				return ent;

		return null;
	}

	public ItemHook getItemHook(ItemStack stack, Player player) {
		for(HookEntity ent : hooks)
			if(ent.getType() == HookEntityType.ITEM && ent.getPlayer().equals(player) && ((ItemHook) ent).getItemStack().equals(stack))
				return (ItemHook) ent;

		return null;
	}

	public ArrayList<HookEntity> getHooks() {
		return hooks;
	}

	public CuukyFrameWork getInstance() {
		return instance;
	}
}