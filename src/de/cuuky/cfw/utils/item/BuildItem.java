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
    private List<String> lore = new ArrayList<>();
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private int amount = 1;
    private boolean deleteAnnotations;

    protected ItemMeta applyMeta(ItemMeta meta, Material type) {
        if (displayName != null && type != Material.AIR) meta.setDisplayName(displayName);
        enchantments.keySet().forEach(ent -> meta.addEnchant(ent, enchantments.get(ent), true));
        if (lore != null) meta.setLore(lore);
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
        this.lore.add(add);
        return this;
    }

    public BuildItem lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public BuildItem lore(String lore) {
        return this.lore(lore == null ? null : lore.split("\n"));
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