package de.cuuky.cfw.hooking.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.hooking.hooks.HookEntityType;
import de.cuuky.cfw.hooking.hooks.chat.ChatHook;
import de.cuuky.cfw.hooking.hooks.item.ItemHook;

public class HookListener implements Listener {

	private HookManager manager;

	public HookListener(HookManager manager) {
		this.manager = manager;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getItem() == null)
			return;

		ItemHook hook = manager.getItemHook(event.getItem(), event.getPlayer());
		if(hook == null)
			return;

		hook.getHookListener().onInteract(event);
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if(event.getPlayer().getItemInHand() == null || event.getRightClicked() == null)
			return;

		ItemHook hook = manager.getItemHook(event.getPlayer().getItemInHand(), event.getPlayer());
		if(hook == null)
			return;

		hook.getHookListener().onInteractEntity(event);
	}

	@EventHandler
	public void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player) || ((Player) event.getDamager()).getItemInHand() == null)
			return;

		Player damager = (Player) event.getDamager();
		ItemHook hook = manager.getItemHook(damager.getItemInHand(), damager);
		if(hook == null)
			return;

		hook.getHookListener().onEntityHit(event);
	}

	@EventHandler
	public void onItemMove(InventoryClickEvent event) {
		ItemHook hook = manager.getItemHook(event.getCurrentItem(), (Player) event.getWhoClicked());
		if(hook != null && !hook.isDragable())
			event.setCancelled(true);
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		ItemHook hook = manager.getItemHook(event.getItemDrop().getItemStack(), event.getPlayer());
		if(hook != null && !hook.isDropable())
			event.setCancelled(true);
	}

	@EventHandler
	public void onItemPick(PlayerPickupItemEvent event) {
		ItemHook hook = manager.getItemHook(event.getItem().getItemStack(), event.getPlayer());
		if(hook != null && !hook.isDropable())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onASyncChat(AsyncPlayerChatEvent event) {
		ChatHook hook = (ChatHook) manager.getHook(HookEntityType.CHAT, event.getPlayer());
		if(hook == null)
			return;

		if(hook.run(event))
			event.setCancelled(true);
	}
}