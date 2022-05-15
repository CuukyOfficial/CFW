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

import de.cuuky.cfw.version.VersionUtils;
import de.cuuky.cfw.version.types.Materials;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class BuildItem {

    private ItemStack stack;
    private Material material;
    private String displayName;
    private List<String> lore;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private int amount = 1;
    private boolean deleteAnnotations;

    protected ItemMeta applyMeta(ItemMeta meta, Material type) {
        if (displayName != null && type != Material.AIR) meta.setDisplayName(displayName);
        enchantments.keySet().forEach(ent -> meta.addEnchant(ent, enchantments.get(ent), true));
        meta.setLore(this.lore);
        return meta;
    }

    public ItemStack build() {
        ItemStack stack = this.stack != null ? this.stack : new ItemStack(this.material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) stack.setItemMeta(this.applyMeta(meta, stack.getType()));
        if (this.deleteAnnotations) VersionUtils.getVersionAdapter().deleteItemAnnotations(stack);
        stack.setAmount(amount);
        return stack;
    }

    public BuildItem amount(int amount) {
        this.amount = amount;
        return this;
    }

    public BuildItem addEnchantment(Enchantment enchantment, int amplifier) {
        if (enchantment == null) return this;
        enchantments.put(enchantment, amplifier);
        return this;
    }

    public BuildItem deleteDamageAnnotation(boolean deleteAnnotations) {
        this.deleteAnnotations = deleteAnnotations;
        return this;
    }

    public BuildItem deleteDamageAnnotation() {
        return this.deleteDamageAnnotation(true);
    }

    public BuildItem material(Material material) {
        this.material = material;
        return this;
    }

    public BuildItem material(Materials material) {
        return this.itemstack(material.parseItem());
    }

    public BuildItem displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public BuildItem itemstack(ItemStack stack) {
        this.stack = stack.clone();
        return this;
    }

    public BuildItem addLore(String add) {
    	if(this.lore == null)
    		this.lore = new ArrayList<>();
        this.lore.add(add);
        return this;
    }

    public BuildItem lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public BuildItem lore(String lore) {
        return this.lore(lore == null || lore.isEmpty() ? null : lore.split("\n"));
    }

    public BuildItem lore(String... lore) {
        return this.lore(lore == null ? null : Arrays.asList(lore));
    }

    public int getAmount() {
        return amount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public boolean shallDeleteAnnotations() {
        return deleteAnnotations;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Material getMaterial() {
        return material;
    }
}