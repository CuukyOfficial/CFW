package de.cuuky.cfw.hooking.hooks.item;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface ItemHookHandler {
	
	public void onEntityHit(EntityDamageByEntityEvent event);

	public void onInteract(PlayerInteractEvent event);

	public void onInteractEntity(PlayerInteractEntityEvent event);

}