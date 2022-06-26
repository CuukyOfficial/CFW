/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.varoplugin.cfw.player.hook.item;

import de.varoplugin.cfw.player.hook.AbstractHookListener;
import de.varoplugin.cfw.player.hook.HookListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerItemHookListener extends AbstractHookListener<ItemHook> implements HookListener<ItemHook> {

    private boolean isPlayerInventory(Inventory inventory) {
        return this.trigger.getPlayer().getInventory().equals(inventory);
    }

    private boolean ignoreEvent(ItemStack stack) {
        return !this.trigger.getItem().equals(stack);
    }

    private boolean ignoreEvent(Player player, ItemStack stack) {
        return !this.trigger.getPlayer().equals(player) || this.ignoreEvent(stack);
    }

    private boolean ignoreEvent(Entity entity, ItemStack stack) {
        return !(entity instanceof Player) || this.ignoreEvent((Player) entity, stack);
    }

    @Override
    public void register(ItemHook trigger) {
        this.trigger = trigger;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (this.ignoreEvent(event.getPlayer(), event.getItem()))
            return;
        this.trigger.eventFired(new HookItemInteractEvent(this.trigger, event));
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (this.ignoreEvent(event.getPlayer(), event.getPlayer().getItemInHand()))
            return;
        this.trigger.eventFired(new HookItemInteractEntityEvent(this.trigger, event));
    }

    @EventHandler
    public void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player damager = (Player) event.getDamager();
        if (this.ignoreEvent(damager, damager.getItemInHand()))
            return;
        this.trigger.eventFired(new HookItemHitEvent(this.trigger, event));
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemMove(InventoryClickEvent event) {
        ItemStack hotKeyed = event.getClick() == ClickType.NUMBER_KEY ? event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) : null;
        if (this.ignoreEvent(event.getWhoClicked(), event.getCurrentItem()) && this.ignoreEvent(event.getCursor()) && (hotKeyed == null || this.ignoreEvent(hotKeyed)))
            return;

        if (this.isPlayerInventory(event.getInventory()) && this.isPlayerInventory(event.getInventory()) && this.trigger.isMovable())
            return;

        event.setCancelled(true);
        ((Player) event.getWhoClicked()).updateInventory();
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemMove(InventoryDragEvent event) {
        if (this.ignoreEvent(event.getWhoClicked(), event.getOldCursor()))
            return;
        event.setCancelled(true);
        ((Player) event.getWhoClicked()).updateInventory();
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (this.trigger.isDroppable() || this.ignoreEvent(event.getPlayer(), event.getItemDrop().getItemStack()))
            return;
        event.setCancelled(true);
    }
}
