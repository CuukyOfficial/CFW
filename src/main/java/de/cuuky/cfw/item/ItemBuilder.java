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

package de.cuuky.cfw.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import de.cuuky.cfw.version.VersionUtils;
import de.cuuky.cfw.version.types.Materials;

/**
 * Use {@link de.cuuky.cfw.utils.item.BuildItem} and {@link de.cuuky.cfw.utils.item.BuildSkull} instead.
 */
@Deprecated
public class ItemBuilder {

    private int amount;
    private String displayName;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments;
    private String playerName;
    private boolean deleteAnnotations;
    private ItemStack pre;
    private Material material;

    public ItemBuilder() {
        amount = 1;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStack build() {
        ItemStack stack = this.pre != null ? this.pre : new ItemStack(this.material);
        ItemMeta stackMeta = stack.getItemMeta();
        if (displayName != null && stack.getType() != Material.AIR) stackMeta.setDisplayName(displayName);

        if (enchantments != null)
            for (Enchantment ent : enchantments.keySet())
                stackMeta.addEnchant(ent, enchantments.get(ent), true);

        if (lore != null) stackMeta.setLore(lore);
        stack.setItemMeta(stackMeta);
        if (this.deleteAnnotations)
            VersionUtils.getVersionAdapter().deleteItemAnnotations(stack);
        stack.setAmount(amount);
        return stack;
    }

    public ItemStack buildSkull() {
        ItemStack stack = Materials.PLAYER_HEAD.parseItem();
        SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();

        skullMeta.setDisplayName(displayName != null ? displayName : playerName);
        skullMeta.setOwner(playerName != null ? playerName : displayName);

        if (lore != null) skullMeta.setLore(lore);
        stack.setItemMeta(skullMeta);
        if (this.deleteAnnotations)
        	VersionUtils.getVersionAdapter().deleteItemAnnotations(stack);
        stack.setAmount(amount);

        return stack;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int amplifier) {
        if (enchantments == null) enchantments = new HashMap<>();
        enchantments.put(enchantment, amplifier);
        return this;
    }

    public ItemBuilder deleteDamageAnnotation() {
        this.deleteAnnotations = true;
        return this;
    }

    public ItemBuilder material(Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder displayname(String displayname) {
        this.displayName = displayname;
        return this;
    }

    public ItemBuilder itemstack(ItemStack stack) {
        this.pre = stack.clone();
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder lore(String lore) {
        this.lore = Collections.singletonList(lore);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public ItemBuilder player(Player player) {
        this.playerName = player.getName();
        return this;
    }

    public ItemBuilder playername(String playername) {
        this.playerName = playername;
        return this;
    }
}