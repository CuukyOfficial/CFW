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

import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

public enum InventoryColor {

    GRAY(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()),
    WHITE(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()),
    BROWN(XMaterial.BROWN_STAINED_GLASS_PANE.parseItem()),
    BLACK(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()),
    RED(XMaterial.RED_STAINED_GLASS_PANE.parseItem()),
    GREEN(XMaterial.GREEN_STAINED_GLASS_PANE.parseItem()),
    BLUE(XMaterial.BLUE_STAINED_GLASS_PANE.parseItem()),
    YELLOW(XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem()),
    MAGENTA(XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem()),
    ORANGE(XMaterial.ORANGE_STAINED_GLASS_PANE.parseItem()),
    PINK(XMaterial.PINK_STAINED_GLASS_PANE.parseItem()),
    PURPLE(XMaterial.PURPLE_STAINED_GLASS_PANE.parseItem()),
    LIME(XMaterial.LIME_STAINED_GLASS_PANE.parseItem()),
    CYAN(XMaterial.CYAN_STAINED_GLASS_PANE.parseItem());

    private final ItemStack colorPane;

    InventoryColor(ItemStack colorPane) {
        this.colorPane = colorPane;
    }

    public ItemStack getColorPane() {
        return colorPane;
    }
}
