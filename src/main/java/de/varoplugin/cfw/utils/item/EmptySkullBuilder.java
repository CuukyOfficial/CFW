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

package de.varoplugin.cfw.utils.item;

import com.cryptomorin.xseries.XMaterial;
import de.varoplugin.cfw.utils.UUIDUtils;
import de.varoplugin.cfw.version.VersionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;
import java.util.UUID;

public class EmptySkullBuilder extends EmptyItemBuilder implements SkullBuilder {

    private UUID uuid;
    private String name;

    public EmptySkullBuilder() {
        super.itemStack(Objects.requireNonNull(XMaterial.PLAYER_HEAD.parseItem()));
    }

    @Override
    public ItemStack build() {
        if (this.getDisplayName() == null && this.name != null)
            this.displayName(this.name);
        return super.build();
    }

    @Override
    protected ItemMeta applyMeta(ItemMeta meta, Material type) {
        assert this.uuid != null;
        SkullMeta sm = (SkullMeta) meta;
        VersionUtils.getVersionAdapter().setOwningPlayer(sm, this.uuid);
        return super.applyMeta(meta, type);
    }

    @Override
    public SkullBuilder itemStack(ItemStack stack) {
        return this;
    }

    @Override
    public SkullBuilder material(Material material) {
        return this;
    }

    @Override
    public SkullBuilder player(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public SkullBuilder fetchPlayer(String name) {
        this.name = name;
        try {
            this.player(UUIDUtils.getUUID(name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public SkullBuilder player(Player player) {
        this.name = player.getName();
        return this.player(player.getUniqueId());
    }
}
