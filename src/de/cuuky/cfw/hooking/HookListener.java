package de.cuuky.cfw.hooking;

import de.cuuky.cfw.hooking.hooks.HookEntityType;
import de.cuuky.cfw.hooking.hooks.chat.ChatHook;
import de.cuuky.cfw.hooking.hooks.item.ItemHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class HookListener implements Listener {

    private final HookManager manager;

    public HookListener(HookManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null)
            return;

        ItemHook hook = manager.getItemHook(event.getItem(), event.getPlayer());
        if (hook == null)
            return;

        hook.getHookListener().onInteract(event);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getItemInHand() == null || event.getRightClicked() == null)
            return;

        ItemHook hook = manager.getItemHook(event.getPlayer().getItemInHand(), event.getPlayer());
        if (hook == null)
            return;

        hook.getHookListener().onInteractEntity(event);
    }

    @EventHandler
    public void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player) || ((Player) event.getDamager()).getItemInHand() == null)
            return;

        Player damager = (Player) event.getDamager();
        ItemHook hook = manager.getItemHook(damager.getItemInHand(), damager);
        if (hook == null)
            return;

        hook.getHookListener().onEntityHit(event);
    }

    private boolean checkDragHook(ItemStack stack, InventoryHolder holder) {
        ItemHook hook = manager.getItemHook(stack, (Player) holder);
        return hook != null && !hook.isDragable();
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemMove(InventoryClickEvent event) {
        if (event.getCurrentItem() == null)
            return;

        if (this.checkDragHook(event.getCurrentItem(), event.getWhoClicked())) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (ItemHook hook : manager.getHooks(HookEntityType.ITEM)) {
                        if (!hook.getPlayer().equals(event.getWhoClicked()))
                            continue;

                        Inventory inv = event.getWhoClicked().getInventory();
                        ItemStack check = inv.getItem(hook.getSlot());
                        if (check != null && check.equals(hook.getItemStack()))
                            continue;

                        event.getClickedInventory().remove(hook.getItemStack());
                        inv.setItem(hook.getSlot(), hook.getItemStack());
                        ((Player) event.getWhoClicked()).updateInventory();
                    }
                }
            }.runTaskLater(manager.getOwnerInstance(), 0L);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemMove(InventoryDragEvent event) {
        if (this.checkDragHook(event.getOldCursor(), event.getWhoClicked())) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemHook hook = manager.getItemHook(event.getItemDrop().getItemStack(), event.getPlayer());
        if (hook != null && !hook.isDropable())
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemPick(PlayerPickupItemEvent event) {
        ItemHook hook = manager.getItemHook(event.getItem().getItemStack(), event.getPlayer());
        if (hook != null && !hook.isDropable())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onASyncChat(AsyncPlayerChatEvent event) {
        ChatHook hook = (ChatHook) manager.getHook(HookEntityType.CHAT, event.getPlayer());
        if (hook == null)
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onASyncChatLow(AsyncPlayerChatEvent event) {
        ChatHook hook = (ChatHook) manager.getHook(HookEntityType.CHAT, event.getPlayer());
        if (hook == null)
            return;

        hook.run(event);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSyncChat(PlayerChatEvent event) {
        ChatHook hook = (ChatHook) manager.getHook(HookEntityType.CHAT, event.getPlayer());
        if (hook == null)
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSyncChatLow(PlayerChatEvent event) {
        ChatHook hook = (ChatHook) manager.getHook(HookEntityType.CHAT, event.getPlayer());
        if (hook == null)
            return;

        hook.run(event);
        event.setCancelled(true);
    }
}