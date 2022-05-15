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