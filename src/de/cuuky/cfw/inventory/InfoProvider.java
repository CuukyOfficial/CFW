package de.cuuky.cfw.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public interface InfoProvider {

    int getPriority();

    List<Info<?>> getProvidedInfos();

    default List<PrioritisedInfo> getPrioritisedInfos() {
        return null;
    }

    default int getSize() {
        return 0;
    }

    default String getTitle() {
        return null;
    }

    default boolean doAnimation() {
        return false;
    }

    default int getHotbarSize() {
        return 0;
    }

    default Consumer<Player> getSoundPlayer() {
        return null;
    }

    default ItemInfo getBackwardsInfo() {
        return null;
    }

    default ItemInfo getForwardsInfo() {
        return null;
    }

    default ItemInfo getCloseInfo() {
        return null;
    }

    default ItemInfo getBackInfo() {
        return null;
    }

    default ItemStack getFillerStack() {
        return null;
    }

    default ItemInserter getInserter() {
        return null;
    }

    default boolean cancelClick() {
        return false;
    }

    default String getPageViewer() {
        return null;
    }
}