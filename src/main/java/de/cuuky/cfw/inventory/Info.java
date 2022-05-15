package de.cuuky.cfw.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Info<T> {

    private static final List<Info<?>> values = new ArrayList<>();

    public static final Info<Integer> SIZE = new Info<>(InfoProvider::getSize);
    public static final Info<String> TITLE = new Info<>(InfoProvider::getTitle);
    public static final Info<Boolean> DO_ANIMATION = new Info<>(InfoProvider::doAnimation);
    public static final Info<Integer> HOTBAR_SIZE = new Info<>(InfoProvider::getHotbarSize);
    public static final Info<Consumer<Player>> PLAY_SOUND = new Info<>(InfoProvider::getSoundPlayer);
    public static final Info<ItemInfo> BACKWARDS_INFO = new Info<>(InfoProvider::getBackwardsInfo);
    public static final Info<ItemInfo> FORWARDS_INFO = new Info<>(InfoProvider::getForwardsInfo);
    public static final Info<ItemInfo> CLOSE_INFO = new Info<>(InfoProvider::getCloseInfo);
    public static final Info<ItemInfo> BACK_INFO = new Info<>(InfoProvider::getBackInfo);
    public static final Info<ItemStack> FILLER_STACK = new Info<>(InfoProvider::getFillerStack);
    public static final Info<ItemInserter> ITEM_INSERTER = new Info<>(InfoProvider::getInserter);
    public static final Info<Boolean> CANCEL_CLICK = new Info<>(InfoProvider::cancelClick);
    public static final Info<String> PAGE_VIEWER = new Info<>(InfoProvider::getPageViewer);

    private final Function<InfoProvider, T> getter;

    Info(Function<InfoProvider, T> getter) {
        this.getter = getter;
        values.add(this);
    }

    public T apply(InfoProvider provider) {
        return this.getter.apply(provider);
    }

    public static List<Info<?>> values() {
        return values;
    }
}