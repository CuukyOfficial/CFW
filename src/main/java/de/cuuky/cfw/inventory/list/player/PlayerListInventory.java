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

package de.cuuky.cfw.inventory.list.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.cryptomorin.xseries.XMaterial;

import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.ItemClick;
import de.cuuky.cfw.inventory.list.AdvancedAsyncListInventory;
import de.cuuky.cfw.utils.item.BuildItem;
import de.cuuky.cfw.utils.item.BuildSkull;
import de.cuuky.cfw.version.VersionUtils;

public class PlayerListInventory extends AdvancedAsyncListInventory<Player> implements Listener {

    private final int size;

    public PlayerListInventory(AdvancedInventoryManager manager, Player player, int size, List<Player> players) {
        super(manager, player, players);

        this.size = size;
        JavaPlugin plugin = this.getManager().getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public PlayerListInventory(AdvancedInventoryManager manager, Player player, int size) {
        this(manager, player, size, new ArrayList<>(VersionUtils.getVersionAdapter().getOnlinePlayers()));
    }

    @Override
    protected HookManager getHookManager() {
        return null;
    }

    @Override
    protected ItemStack getLoadingItem() {
        return new BuildItem().material(XMaterial.SKELETON_SKULL).displayName("§cLoading...").build();
    }

    @Override
    protected ItemStack getItemStack(Player item) {
        return new BuildSkull().player(item).build();
    }

    @Override
    protected ItemClick getClick(Player item) {
        return event -> {
            this.getPlayer().teleport(item);
            this.close();
        };
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.getList().add(event.getPlayer());
        this.update();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.getList().remove(event.getPlayer());
        this.update();
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public String getTitle() {
        return "Online players";
    }
}
