package de.cuuky.cfw.inventory;

import de.cuuky.cfw.inventory.inserter.DirectInserter;
import de.cuuky.cfw.utils.item.BuildItem;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.cfw.version.types.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface DefaultInfoProvider extends InfoProvider {

    AdvancedInventory getUser();

    @Override
    default int getPriority() {
        return 0;
    }

    @Override
    default String getTitle() {
        return "Inventory";
    }

    @Override
    default int getSize() {
        return 36;
    }

    @Override
    default boolean doAnimation() {
        return !this.getUser().isOpen();
    }

    @Override
    default int getHotbarSize() {
        return 9;
    }

    @Override
    default Consumer<Player> getSoundPlayer() {
        return (player) -> {
            ItemStack clicked = this.getUser().getInventory().getItem(this.getUser().getLastClickedSlot());
            if (clicked != null && clicked.getType() != Material.AIR)
                player.playSound(player.getLocation(), Sounds.CLICK.bukkitSound(), 1, 1);
        };
    }

    @Override
    default ItemInfo getBackwardsInfo() {
        return new ItemInfo(this.getUser().getUsableSize(), new BuildItem().material(Material.ARROW)
                .displayName("§cBackwards " + this.getUser().getInfo(Info.PAGE_VIEWER)).build());
    }

    @Override
    default ItemInfo getForwardsInfo() {
        return new ItemInfo(this.getUser().getUsableSize() + 8, new BuildItem().material(Material.ARROW)
                .displayName("§aForwards " + this.getUser().getInfo(Info.PAGE_VIEWER)).build());
    }

    @Override
    default ItemInfo getCloseInfo() {
        return new ItemInfo(this.getUser().getUsableSize() + 4, new BuildItem()
                .material(Materials.REDSTONE).displayName("§4Close").build());
    }

    @Override
    default ItemInfo getBackInfo() {
        return new ItemInfo(this.getUser().getUsableSize() + 3, new BuildItem()
                .material(Material.STONE_BUTTON).displayName("§cBack").build());
    }

    @Override
    default ItemStack getFillerStack() {
        return new BuildItem().displayName("§c").material(Materials.BLACK_STAINED_GLASS_PANE).build();
    }

    @Override
    default ItemInserter getInserter() {
        return new DirectInserter();
    }

    @Override
    default boolean cancelClick() {
        return true;
    }

    @Override
    default String getPageViewer() {
        return "§7" + this.getUser().getPage() +
                "§8/§7" + (this.getUser().getMaxPage() == Integer.MAX_VALUE ? "X" : this.getUser().getMaxPage());
    }
}