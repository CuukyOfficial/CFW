package de.cuuky.cfw.inventory.list.player;

import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.ItemClick;
import de.cuuky.cfw.inventory.list.AdvancedAsyncListInventory;
import de.cuuky.cfw.utils.item.BuildItem;
import de.cuuky.cfw.utils.item.BuildSkull;
import de.cuuky.cfw.version.VersionUtils;
import de.cuuky.cfw.version.types.Materials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PlayerListInventory extends AdvancedAsyncListInventory<Player> implements Listener {

    private final int size;

    public PlayerListInventory(AdvancedInventoryManager manager, Player player, int size, List<Player> players) {
        super(manager, player, players);

        this.size = size;
        JavaPlugin plugin = this.getManager().getOwnerInstance();
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
    protected void addListItem(int index, Player player) {
        super.addItem(index, new BuildItem().material(Materials.SKELETON_SKULL).displayName(player.getName()).build());
        super.addListItem(index, player);
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