package de.cuuky.cfw.inventory;

import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.version.types.Materials;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

interface ItemOverrideable {

    default String getPageViewer(AdvancedInventory inv) {
        return "§7" + inv.getPage() + "§8/§7" + inv.getMaxPage();
    }

    default ItemStack getBackwardsStack(AdvancedInventory inv) {
        return new ItemBuilder().material(Material.ARROW).displayname("§cBackwards " + getPageViewer(inv)).build();
    }

    default ItemStack getForwardStack(AdvancedInventory inv) {
        return new ItemBuilder().material(Material.ARROW).displayname("§aForwards " + getPageViewer(inv)).build();
    }

    default ItemStack getCloseStack(AdvancedInventory inv) {
        return new ItemBuilder().material(Materials.REDSTONE.parseMaterial()).displayname("§4Close").build();
    }

    default ItemStack getBackStack(AdvancedInventory inv) {
        return new ItemBuilder().material(Material.STONE_BUTTON).displayname("§fBack to '" + inv.getPrevious().getTitle() + "§f'").build();
    }

    default ItemStack getFillerStack(AdvancedInventory inv) {
        return new ItemBuilder().displayname("§c").itemstack(new ItemStack(Materials.BLACK_STAINED_GLASS_PANE.parseMaterial(), 1, (short) 15)).build();
    }
}