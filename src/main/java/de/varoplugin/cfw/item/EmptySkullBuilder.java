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

import com.cryptomorin.xseries.XMaterial;
import de.varoplugin.cfw.version.VersionUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class EmptySkullBuilder implements SkullBuilder {

    private final ItemBuilder itemBuilder;

    public EmptySkullBuilder() {
        this.itemBuilder = new EmptyItemBuilder().itemStack(Objects.requireNonNull(XMaterial.PLAYER_HEAD.parseItem()));
    }

    private ItemMeta applyMeta(ItemMeta meta, UUID uuid) {
        SkullMeta sm = (SkullMeta) meta;
        VersionUtils.getVersionAdapter().setOwningPlayer(sm, uuid);
        return sm;
    }

    @Override
    public ItemStack build(UUID uuid) {
        ItemStack built = this.itemBuilder.build();
        built.setItemMeta(this.applyMeta(built.getItemMeta(), uuid));
        return built;
    }

    @Override
    public ItemStack build(Player player) {
        return this.build(player.getUniqueId());
    }

    @Override
    public SkullBuilder amount(int amount) {
        this.itemBuilder.amount(amount);
        return this;
    }

    @Override
    public SkullBuilder addEnchantment(Enchantment enchantment, int amplifier) {
        this.itemBuilder.addEnchantment(enchantment, amplifier);
        return this;
    }

    @Override
    public SkullBuilder deleteDamageAnnotation(boolean deleteAnnotations) {
        this.itemBuilder.deleteDamageAnnotation(deleteAnnotations);
        return this;
    }

    @Override
    public SkullBuilder deleteDamageAnnotation() {
        this.itemBuilder.deleteDamageAnnotation();
        return this;
    }

    @Override
    public SkullBuilder displayName(String displayName) {
        this.itemBuilder.displayName(displayName);
        return this;
    }

    @Override
    public SkullBuilder addLore(String add) {
        this.itemBuilder.addLore(add);
        return this;
    }

    @Override
    public SkullBuilder lore(List<String> lore) {
        this.itemBuilder.lore(lore);
        return this;
    }

    @Override
    public SkullBuilder lore(String lore) {
        this.itemBuilder.lore(lore);
        return this;
    }

    @Override
    public SkullBuilder lore(String... lore) {
        this.itemBuilder.lore(lore);
        return this;
    }

    @Override
    public int getAmount() {
        return this.itemBuilder.getAmount();
    }

    @Override
    public String getDisplayName() {
        return this.itemBuilder.getDisplayName();
    }

    @Override
    public List<String> getLore() {
        return this.itemBuilder.getLore();
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return this.itemBuilder.getEnchantments();
    }

    @Override
    public boolean shallDeleteAnnotations() {
        return this.itemBuilder.shallDeleteAnnotations();
    }

    @Override
    public ItemStack getStack() {
        return this.itemBuilder.getStack();
    }

    @Override
    public Material getMaterial() {
        return this.itemBuilder.getMaterial();
    }
}
