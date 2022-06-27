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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

class OneTwelveVersionAdapter extends OneNineVersionAdapter {

    private Method localeMethod;
    protected Object messageTypeSystem;
    protected Object messageTypeGameInfo;

    @Override
    protected void initLocale() throws SecurityException, IllegalArgumentException {
        try {
            this.localeMethod = Class.forName("org.bukkit.craftbukkit." + VersionUtils.getNmsVersion() + ".entity.CraftPlayer").getDeclaredMethod("getLocale");
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initPacketChatArgConstructor() throws NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        Class<?> messageTypeClass = Class.forName(VersionUtils.getNmsClass() + ".ChatMessageType");
        this.messageTypeSystem = messageTypeClass.getDeclaredField("SYSTEM").get(null);
        this.messageTypeGameInfo = messageTypeClass.getDeclaredField("GAME_INFO").get(null);
        this.packetChatConstructor = this.packetChatClass.getConstructor(this.chatBaseComponentInterface, messageTypeClass);
    }

    @Override
    protected void initTitle() throws IllegalArgumentException, SecurityException {
    }

    @Override
    protected Object getActionbarPacket(Player player, Object text) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return this.packetChatConstructor.newInstance(text, this.messageTypeGameInfo);
    }

    @Override
    protected Object getMessagePacket(Player player, Object text) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return this.packetChatConstructor.newInstance(text, this.messageTypeSystem);
    }

    @Override
    public void setOwningPlayer(SkullMeta skullMeta, UUID uuid) {
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
    }

    @Override
    public void sendBlockChange(Player player, Location location, Material material) {
        player.sendBlockChange(location, material.createBlockData());
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle) {
        player.sendTitle(title, subtitle, 10, 70, 20);
    }

    @Override
    public String getLocale(Player player) {
        try {
            return (String) this.localeMethod.invoke(player);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new Error();
        }
    }
}
