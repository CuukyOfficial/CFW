package de.cuuky.cfw.inventory.list.player;

import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.ItemClick;
import de.cuuky.cfw.inventory.list.AdvancedAsyncListInventory;
import de.cuuky.cfw.item.ItemBuilder;
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

    public PlayerListInventory(AdvancedInventoryManager manager, Player player, int size, List<Player> players) {
        super(manager, player, size, players);

        JavaPlugin plugin = this.getManager().getOwnerInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public PlayerListInventory(AdvancedInventoryManager manager, Player player, int size) {
        this(manager, player, size, new ArrayList<>(manager.getOwnerInstance().getServer().getOnlinePlayers()));
    }

    @Override
    protected void addListItem(int start, int index) {
        Player player = this.getList().get(start + index);
        super.addItem(index, new ItemBuilder().material(Materials.SKELETON_SKULL.parseMaterial()).displayname(player.getName()).build());
        super.addListItem(start, index);
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
    protected String getTitle() {
        return "Online players";
    }

    @Override
    protected ItemStack getItemStack(Player item) {
        return new ItemBuilder().player(item).buildSkull();
    }

    @Override
    protected ItemClick getClick(Player item) {
        return event -> {
            this.getPlayer().teleport(item);
            this.close();
        };
    }
}