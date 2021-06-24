package de.cuuky.cfw.hooking.hooks.item;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface ItemHookHandler {

	void onEntityHit(EntityDamageByEntityEvent event);

	void onInteract(PlayerInteractEvent event);

	void onInteractEntity(PlayerInteractEntityEvent event);

}