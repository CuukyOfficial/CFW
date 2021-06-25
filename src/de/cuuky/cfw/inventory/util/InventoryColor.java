package de.cuuky.cfw.inventory.util;

import de.cuuky.cfw.serialize.identifiers.CFWSerializeField;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.version.types.Materials;
import org.bukkit.inventory.ItemStack;

public enum InventoryColor implements CFWSerializeable {

    @CFWSerializeField(enumValue = "GRAY")
    GRAY(Materials.GRAY_STAINED_GLASS_PANE.parseItem()),

    @CFWSerializeField(enumValue = "WHITE")
    WHITE(Materials.WHITE_STAINED_GLASS_PANE.parseItem()),

    @CFWSerializeField(enumValue = "BROWN")
    BROWN(Materials.BROWN_STAINED_GLASS_PANE.parseItem()),

    @CFWSerializeField(enumValue = "BLACK")
    BLACK(Materials.BLACK_STAINED_GLASS_PANE.parseItem()),

    @CFWSerializeField(enumValue = "RED")
    RED(Materials.RED_STAINED_GLASS_PANE.parseItem()),

    @CFWSerializeField(enumValue = "GREEN")
    GREEN(Materials.GREEN_STAINED_GLASS_PANE.parseItem()),

    @CFWSerializeField(enumValue = "BLUE")
    BLUE(Materials.BLUE_STAINED_GLASS_PANE.parseItem()),

    @CFWSerializeField(enumValue = "YELLOW")
    YELLOW(Materials.YELLOW_STAINED_GLASS_PANE.parseItem()),

    @CFWSerializeField(enumValue = "MAGENTA")
    MAGENTA(Materials.MAGENTA_STAINED_GLASS_PANE.parseItem()),

    @CFWSerializeField(enumValue = "ORANGE")
    ORANGE(Materials.ORANGE_STAINED_GLASS_PANE.parseItem()),

    @CFWSerializeField(enumValue = "PINK")
    PINK(Materials.PINK_STAINED_GLASS_PANE.parseItem()),

    @CFWSerializeField(enumValue = "PURPLE")
    PURPLE(Materials.PURPLE_STAINED_GLASS_PANE.parseItem()),

    @CFWSerializeField(enumValue = "LIME")
    LIME(Materials.LIME_STAINED_GLASS_PANE.parseItem()),

    @CFWSerializeField(enumValue = "CYAN")
    CYAN(Materials.CYAN_STAINED_GLASS_PANE.parseItem());

    private final ItemStack colorPane;

    InventoryColor(ItemStack colorPane) {
        this.colorPane = colorPane;
    }

    public ItemStack getColorPane() {
        return colorPane;
    }
}