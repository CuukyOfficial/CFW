package de.cuuky.cfw.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import de.cuuky.cfw.utils.JavaUtils;
import de.cuuky.cfw.version.VersionUtils;
import de.cuuky.cfw.version.types.Materials;

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
        if (displayName != null && stack.getType() != Material.AIR)
            stackMeta.setDisplayName(displayName);

        if (lore != null)
            stackMeta.setLore(lore);

        if (enchantments != null)
            for (Enchantment ent : enchantments.keySet())
                stackMeta.addEnchant(ent, enchantments.get(ent), true);

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

        if (lore != null)
            skullMeta.setLore(lore);

        stack.setItemMeta(skullMeta);
        if (this.deleteAnnotations)
        	VersionUtils.getVersionAdapter().deleteItemAnnotations(stack);
        stack.setAmount(amount);

        return stack;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int amplifier) {
        if (enchantments == null)
            enchantments = new HashMap<>();

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
        this.pre = stack;
        return this;
    }

    public ItemBuilder lore(ArrayList<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder lore(String lore) {
        this.lore = JavaUtils.collectionToArray(new String[]{lore});
        return this;
    }

    public ItemBuilder lore(String... lore) {
        this.lore = JavaUtils.collectionToArray(lore);
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