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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cryptomorin.xseries.XMaterial;

import de.varoplugin.cfw.version.VersionUtils;

class ItemBuilderImpl implements ItemBuilder {

    private final ItemStack itemStack;
    private String displayName;
    private List<String> lore;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private int amount = 1;
    private boolean deleteAnnotations;

    ItemBuilderImpl(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    ItemBuilderImpl(Material material) {
        this(new ItemStack(material));
    }

    ItemBuilderImpl(XMaterial material) {
        this(Objects.requireNonNull(material.parseItem()));
    }

    protected ItemMeta applyMeta(ItemMeta meta, Material type) {
        if (this.displayName != null && type != Material.AIR)
            meta.setDisplayName(this.displayName);
        this.enchantments.keySet().forEach(ent -> meta.addEnchant(ent, this.enchantments.get(ent), true));
        meta.setLore(this.lore);
        return meta;
    }

    @Override
    public ItemStack build() {
        ItemStack stack = this.itemStack;
        ItemMeta meta = stack.getItemMeta();
        if (meta != null)
            stack.setItemMeta(this.applyMeta(meta, stack.getType()));
        if (this.deleteAnnotations)
            VersionUtils.getVersionAdapter().deleteItemAnnotations(stack);
        stack.setAmount(this.amount);
        return stack;
    }

    @Override
    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public ItemBuilder addEnchantment(Enchantment enchantment, int amplifier) {
        if (enchantment == null)
            return this;
        this.enchantments.put(enchantment, amplifier);
        return this;
    }

    @Override
    public ItemBuilder deleteDamageAnnotation(boolean deleteAnnotations) {
        this.deleteAnnotations = deleteAnnotations;
        return this;
    }

    @Override
    public ItemBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    public ItemBuilder addLore(String add) {
        if (this.lore == null)
            this.lore = new ArrayList<>();
        this.lore.add(add);
        return this;
    }

    @Override
    public ItemBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    @Override
    public ItemBuilder lore(String... lore) {
        return this.lore(lore == null ? null : Arrays.asList(lore));
    }

    @Override
    public ItemBuilder lore(String lore) {
        return this.lore(lore == null ? null : new String[] { lore });
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public List<String> getLore() {
        return this.lore;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return this.enchantments;
    }

    @Override
    public boolean shallDeleteAnnotations() {
        return this.deleteAnnotations;
    }

    @Override
    public ItemStack getStack() {
        return this.itemStack;
    }

    @Override
    public Material getMaterial() {
        return this.itemStack.getType();
    }
}
