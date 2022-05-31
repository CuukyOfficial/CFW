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

package de.cuuky.cfw.utils.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import de.cuuky.cfw.version.types.Materials;

public class BuildSkull extends BuildItem {

    private String name;

    public BuildSkull() {
        super.itemstack(Materials.PLAYER_HEAD.parseItem());
    }

    @Override
    public ItemStack build() {
        if (this.getDisplayName() == null && this.name != null)
            this.displayName(this.name);
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
