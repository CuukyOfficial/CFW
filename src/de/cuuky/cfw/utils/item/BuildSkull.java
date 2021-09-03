package de.cuuky.cfw.utils.item;

import de.cuuky.cfw.version.types.Materials;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class BuildSkull extends BuildItem {

    private OfflinePlayer player;
    private String name;

    public BuildSkull() {
        this.itemstack(Materials.PLAYER_HEAD.parseItem());
    }

    @Override
    public ItemStack build() {
        if (this.getDisplayName() == null && this.name != null) this.displayName(this.name);
        return super.build();
    }

    @Override
    protected ItemMeta applyMeta(ItemMeta meta, Material type) {
        SkullMeta sm = (SkullMeta) meta;
        if (this.player != null) sm.setOwningPlayer(this.player);
        else if (this.name != null) sm.setOwner(this.name);
        return super.applyMeta(meta, type);
    }

    /**
     * Note: this won't have any effect
     */
    @Override
    public BuildItem itemstack(ItemStack stack) {
        return this;
    }

    /**
     * Note: this won't have any effect
     */
    @Override
    public BuildItem material(Material material) {
        return this;
    }

    public BuildItem player(Player player) {
        this.player = player;
        return this.name(player.getName());
    }

    public BuildItem name(String name) {
        this.name = name;
        return this;
    }
}