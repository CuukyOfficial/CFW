package de.cuuky.cfw.inventory;

import de.cuuky.cfw.inventory.inserter.DirectInserter;
import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.cfw.version.types.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

abstract class DefaultInfoProvider implements InfoProvider {

    abstract AdvancedInventory getUser();

    @Override
    public final List<Info<?>> getProvidedInfos() {
        return Info.values();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getTitle() {
        return "Inventory";
    }

    @Override
    public int getSize() {
        return 36;
    }

    @Override
    public boolean doAnimation() {
        return !this.getUser().isOpen();
    }

    @Override
    public int getHotbarSize() {
        return 9;
    }

    @Override
    public Consumer<Player> getSoundPlayer() {
        return (player) -> player.playSound(player.getLocation(), Sounds.CLICK.bukkitSound(), 1, 1);
    }

    @Override
    public ItemInfo getBackwardsInfo() {
        return new ItemInfo(this.getUser().getUsableSize(), new ItemBuilder().material(Material.ARROW)
                .displayname("§cBackwards " + this.getUser().getInfo(Info.PAGE_VIEWER)).build());
    }

    @Override
    public ItemInfo getForwardsInfo() {
        return new ItemInfo(this.getUser().getUsableSize() + 8, new ItemBuilder().material(Material.ARROW)
                .displayname("§aForwards " + this.getUser().getInfo(Info.PAGE_VIEWER)).build());
    }

    @Override
    public ItemInfo getCloseInfo() {
        return new ItemInfo(this.getUser().getUsableSize() + 4, new ItemBuilder()
                .material(Materials.REDSTONE.parseMaterial()).displayname("§4Close").build());
    }

    @Override
    public ItemInfo getBackInfo() {
        return new ItemInfo(this.getUser().getUsableSize() + 3, new ItemBuilder()
                .material(Material.STONE_BUTTON).displayname("§cBack").build());
    }

    @Override
    public ItemStack getFillerStack() {
        return new ItemBuilder().displayname("§c").itemstack(Materials.BLACK_STAINED_GLASS_PANE.parseItem()).build();
    }

    @Override
    public ItemInserter getInserter() {
        return new DirectInserter();
    }

    @Override
    public boolean cancelClick() {
        return true;
    }

    @Override
    public String getPageViewer() {
        return "§7" + this.getUser().getPage() + "§8/§7" + this.getUser().getMaxPage();
    }
}