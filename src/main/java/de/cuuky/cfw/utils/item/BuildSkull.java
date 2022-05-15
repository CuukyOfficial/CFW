package de.cuuky.cfw.utils.item;

import de.cuuky.cfw.version.types.Materials;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class BuildSkull extends BuildItem {

    private String name;

    public BuildSkull() {
        super.itemstack(Materials.PLAYER_HEAD.parseItem());
    }

    @Override
    public ItemStack build() {
        if (this.getDisplayName() == null && this.name != null) this.displayName(this.name);
        return super.build();
    }

    @Override
    protected ItemMeta applyMeta(ItemMeta meta, Material type) {
        assert this.name != null;
        SkullMeta sm = (SkullMeta) meta;
        sm.setOwner(this.name);
        return super.applyMeta(meta, type);
    }

    /**
     * Note: this won't have any effect
     */
    @Override
    public BuildSkull itemstack(ItemStack stack) {
        return this;
    }

    /**
     * Note: this won't have any effect
     */
    @Override
    public BuildSkull material(Material material) {
        return this;
    }

    public BuildSkull player(String name) {
        this.name = name;
        return this;
    }

    public BuildSkull player(Player player) {
        return this.player(player.getName());
    }

    @Override
    public BuildSkull displayName(String displayName) {
        return (BuildSkull) super.displayName(displayName);
    }

    @Override
    public BuildSkull amount(int amount) {
        return (BuildSkull) super.amount(amount);
    }
}