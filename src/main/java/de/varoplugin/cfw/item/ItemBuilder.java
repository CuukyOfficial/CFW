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

package de.varoplugin.cfw.item;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.varoplugin.cfw.version.ServerVersion;
import de.varoplugin.cfw.version.VersionUtils;

public interface ItemBuilder {

    ItemStack build();

    ItemBuilder amount(int amount);

    ItemBuilder addEnchantment(Enchantment enchantment, int amplifier);

    ItemBuilder deleteDamageAnnotation(boolean deleteAnnotations);

    default ItemBuilder deleteDamageAnnotation() {
        return this.deleteDamageAnnotation(true);
    }

    ItemBuilder displayName(String displayName);

    ItemBuilder addLore(String add);

    ItemBuilder lore(List<String> lore);

    ItemBuilder lore(String... lore);

    int getAmount();

    String getDisplayName();

    List<String> getLore();

    Map<Enchantment, Integer> getEnchantments();

    boolean shallDeleteAnnotations();

    ItemStack getStack();

    Material getMaterial();

    public static ItemBuilder material(Material material) {
        return new ItemBuilderImpl(material);
    }

    public static ItemBuilder material(XMaterial material) {
        return new ItemBuilderImpl(material);
    }

    public static ItemBuilder itemStack(ItemStack itemStack) {
        return new ItemBuilderImpl(itemStack);
    }

    public static ItemBuilder skull(UUID uuid) {
        if (VersionUtils.getVersion().isLowerThan(ServerVersion.ONE_12))
            return skull(uuid.toString());
        return SkullBuilderImpl.uuid(uuid);
    }

    public static ItemBuilder skull(String name) {
        return SkullBuilderImpl.name(name);
    }

    public static ItemBuilder skull(Player player) {
        return skull(player.getUniqueId());
    }
}
