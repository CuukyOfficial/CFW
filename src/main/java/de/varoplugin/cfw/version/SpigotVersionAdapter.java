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

package de.varoplugin.cfw.version;

import java.util.Collection;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

import net.md_5.bungee.api.chat.ClickEvent;

public class SpigotVersionAdapter implements VersionAdapter {

    private final VersionAdapter parent;

    public SpigotVersionAdapter(Supplier<VersionAdapter> parentSupplier) {
        this.parent = parentSupplier.get();
    }

    @Override
    public Collection<? extends Player> getOnlinePlayers() {
        return this.parent.getOnlinePlayers();
    }

    @Override
    public Object getConnection(Player player) {
        return this.parent.getConnection(player);
    }

    @Override
    public int getPing(Player player) {
        return this.parent.getPing(player);
    }

    @Override
    public void respawnPlayer(Player player) {
        this.parent.respawnPlayer(player);
    }

    @Override
    public void sendActionbar(Player player, String message, int duration, Plugin instance) {
        this.parent.sendActionbar(player, message, duration, instance);
    }

    @Override
    public void sendActionbar(Player player, String message) {
        this.parent.sendActionbar(player, message);
    }

    @Override
    public void sendClickableMessage(Player player, String message, ClickEvent.Action action, String value) {
        this.parent.sendClickableMessage(player, message, action, value);
    }

    @Override
    public void sendLinkedMessage(Player player, String message, String link) {
        this.parent.sendLinkedMessage(player, message, link);
    }

    @Override
    public void sendTablist(Player player, String header, String footer) {
        this.parent.sendTablist(player, header, footer);
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle) {
        this.parent.sendTitle(player, title, subtitle);
    }

    @Override
    public void setOwningPlayer(SkullMeta skullMeta, UUID uuid) {
        this.parent.setOwningPlayer(skullMeta, uuid);
    }

    @Override
    public void sendBlockChange(Player player, Location location, Material material) {
        this.parent.sendBlockChange(player, location, material);
    }

    @Override
    public void setAttributeSpeed(Player player, double value) {
        this.parent.setAttributeSpeed(player, value);
    }

    @Override
    public void setNameTagVisibility(Team team, boolean shown) {
        this.parent.setNameTagVisibility(team, shown);
    }

    @Override
    public void setArmorStandAttributes(Entity armorStand, boolean visible, boolean customNameVisible, boolean gravity, String customName) {
        this.parent.setArmorStandAttributes(armorStand, visible, customNameVisible, gravity, customName);
    }

    @Override
    public void removeAi(LivingEntity entity) {
        this.parent.removeAi(entity);
    }

    @Override
    public void setXpCooldown(Player player, int cooldown) {
        this.parent.setXpCooldown(player, cooldown);
    }

    @Override
    public void deleteItemAnnotations(ItemStack item) {
        this.parent.deleteItemAnnotations(item);
    }

    @Override
    public ItemStack getItemInUse(Player player) {
        return this.parent.getItemInUse(player);
    }

    @Override
    public BlockFace getSignAttachedFace(Block block) {
        return this.parent.getSignAttachedFace(block);
    }

    @Override
    public Object getNetworkManager(Player player) {
        return this.parent.getNetworkManager(player);
    }

    @Override
    public String getLocale(Player player) {
        return this.parent.getLocale(player);
    }

    @Override
    public void forceClearWorlds() {
        this.parent.forceClearWorlds();
    }

    @Override
    public Properties getServerProperties() {
        return this.parent.getServerProperties();
    }

    @Override
    public void setServerProperty(String key, Object value) {
        this.parent.setServerProperty(key, value);
    }
}
