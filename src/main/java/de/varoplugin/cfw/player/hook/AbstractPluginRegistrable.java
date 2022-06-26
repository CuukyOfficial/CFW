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

package de.varoplugin.cfw.player.hook;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class AbstractPluginRegistrable implements PlayerRegistrable {

    private Plugin plugin;
    private Player player;

    @Override
    public boolean isRegistered() {
        return this.plugin != null;
    }

    protected void onRegister(Player player, Plugin plugin) {
        this.plugin = plugin;
        this.player = player;
    }

    protected void onUnregister() {
        this.plugin = null;
    }

    @Override
    public boolean register(Player player, Plugin plugin) {
        if (this.plugin != null)
            return false;
        this.onRegister(player, plugin);
        return true;
    }

    @Override
    public boolean unregister() {
        if (this.plugin == null)
            return false;
        this.onUnregister();
        return true;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }
}
