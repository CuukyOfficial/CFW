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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

import net.md_5.bungee.api.chat.ClickEvent;

public class UnsupportedVersionAdapter implements VersionAdapter {

    @Override
    public Collection<? extends Player> getOnlinePlayers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getConnection(Player player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPing(Player player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void respawnPlayer(Player player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendActionbar(Player player, String message, int duration, Plugin instance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendActionbar(Player player, String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendClickableMessage(Player player, String message, ClickEvent.Action action, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendLinkedMessage(Player player, String message, String link) {
        this.sendClickableMessage(player, message, null, link);
    }

    @Override
    public void sendTablist(Player player, String header, String footer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendBlockChange(Player player, Location location, Material material) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttributeSpeed(Player player, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNameTagVisibility(Team team, boolean shown) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setArmorStandAttributes(Entity armorStand, boolean visible, boolean customNameVisible, boolean gravity, String customName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAi(LivingEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setXpCooldown(Player player, int cooldown) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteItemAnnotations(ItemStack item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack getItemInUse(Player player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockFace getSignAttachedFace(Block block) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getNetworkManager(Player player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocale(Player player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forceClearWorlds() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Properties getServerProperties() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setServerProperty(String key, Object value) {
        throw new UnsupportedOperationException();
    }
}
