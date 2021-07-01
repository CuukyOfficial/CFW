package de.cuuky.cfw.inventory;

import de.cuuky.cfw.inventory.inserter.DirectInserter;
import de.cuuky.cfw.item.ItemBuilder;
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
        return (player) -> player.playSound(player.getLocation(), Sounds.CLICK.bukkitSound(), 1, 1);
    }

    @Override
    default ItemInfo getBackwardsInfo() {
        return new ItemInfo(this.getUser().getUsableSize(), new ItemBuilder().material(Material.ARROW)
                .displayname("§cBackwards " + this.getUser().getInfo(Info.PAGE_VIEWER)).build());
    }

    @Override
    default ItemInfo getForwardsInfo() {
        return new ItemInfo(this.getUser().getUsableSize() + 8, new ItemBuilder().material(Material.ARROW)
                .displayname("§aForwards " + this.getUser().getInfo(Info.PAGE_VIEWER)).build());
    }

    @Override
    default ItemInfo getCloseInfo() {
        return new ItemInfo(this.getUser().getUsableSize() + 4, new ItemBuilder()
                .material(Materials.REDSTONE.parseMaterial()).displayname("§4Close").build());
    }

    @Override
    default ItemInfo getBackInfo() {
        return new ItemInfo(this.getUser().getUsableSize() + 3, new ItemBuilder()
                .material(Material.STONE_BUTTON).displayname("§cBack").build());
    }

    @Override
    default ItemStack getFillerStack() {
        return new ItemBuilder().displayname("§c").itemstack(Materials.BLACK_STAINED_GLASS_PANE.parseItem()).build();
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
        return "§7" + this.getUser().getPage() + "§8/§7" + this.getUser().getMaxPage();
    }
}