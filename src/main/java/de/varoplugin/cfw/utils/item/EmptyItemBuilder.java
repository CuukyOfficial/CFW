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
import de.varoplugin.cfw.version.VersionUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EmptyItemBuilder implements ItemBuilder {

    private ItemStack stack;
    private Material material;
    private String displayName;
    private List<String> lore;
    private final Map<Enchantment, Integer> enchantments;
    private int amount = 1;
    private boolean deleteAnnotations;

    public EmptyItemBuilder() {
        this.enchantments = new HashMap<>();
    }

    protected ItemMeta applyMeta(ItemMeta meta, Material type) {
        if (displayName != null && type != Material.AIR)
            meta.setDisplayName(displayName);
        enchantments.keySet().forEach(ent -> meta.addEnchant(ent, enchantments.get(ent), true));
        meta.setLore(this.lore);
        return meta;
    }

    @Override
    public ItemStack build() {
        ItemStack stack = this.stack != null ? this.stack : new ItemStack(this.material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null)
            stack.setItemMeta(this.applyMeta(meta, stack.getType()));
        if (this.deleteAnnotations)
            VersionUtils.getVersionAdapter().deleteItemAnnotations(stack);
        stack.setAmount(amount);
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
        enchantments.put(enchantment, amplifier);
        return this;
    }

    @Override
    public ItemBuilder deleteDamageAnnotation(boolean deleteAnnotations) {
        this.deleteAnnotations = deleteAnnotations;
        return this;
    }

    @Override
    public ItemBuilder deleteDamageAnnotation() {
        return this.deleteDamageAnnotation(true);
    }

    @Override
    public ItemBuilder material(Material material) {
        this.material = material;
        return this;
    }

    @Override
    public ItemBuilder material(XMaterial material) {
        return this.itemStack(Objects.requireNonNull(material.parseItem()));
    }

    @Override
    public ItemBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    public ItemBuilder itemStack(ItemStack stack) {
        this.stack = stack.clone();
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
    public ItemBuilder lore(String lore) {
        return this.lore(lore == null || lore.isEmpty() ? null : lore.split("\n"));
    }

    @Override
    public ItemBuilder lore(String... lore) {
        return this.lore(lore == null ? null : Arrays.asList(lore));
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public List<String> getLore() {
        return lore;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    @Override
    public boolean shallDeleteAnnotations() {
        return deleteAnnotations;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public Material getMaterial() {
        return material;
    }
}
