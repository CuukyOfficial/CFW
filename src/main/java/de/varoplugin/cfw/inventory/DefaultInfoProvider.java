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

package de.varoplugin.cfw.inventory;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import de.varoplugin.cfw.inventory.inserter.DirectInserter;
import de.varoplugin.cfw.item.EmptyItemBuilder;

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
            if (clicked != null && clicked.getType() != XMaterial.AIR.parseMaterial())
                player.playSound(player.getLocation(), XSound.UI_BUTTON_CLICK.parseSound(), 1, 1);
        };
    }

    @Override
    default ItemInfo getBackwardsInfo() {
        return new ItemInfo(this.getUser().getUsableSize(), new EmptyItemBuilder().material(XMaterial.ARROW).displayName("§cBackwards " + this.getUser().getInfo(Info.PAGE_VIEWER)).build());
    }

    @Override
    default ItemInfo getForwardsInfo() {
        return new ItemInfo(this.getUser().getUsableSize() + 8, new EmptyItemBuilder().material(XMaterial.ARROW).displayName("§aForwards " + this.getUser().getInfo(Info.PAGE_VIEWER)).build());
    }

    @Override
    default ItemInfo getCloseInfo() {
        return new ItemInfo(this.getUser().getUsableSize() + 4, new EmptyItemBuilder().material(XMaterial.REDSTONE).displayName("§4Close").build());
    }

    @Override
    default ItemInfo getBackInfo() {
        return new ItemInfo(this.getUser().getUsableSize() + 3, new EmptyItemBuilder().material(XMaterial.STONE_BUTTON).displayName("§cBack").build());
    }

    @Override
    default ItemStack getFillerStack() {
        return new EmptyItemBuilder().displayName("§c").material(XMaterial.BLACK_STAINED_GLASS_PANE).build();
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
        return "§7" + this.getUser().getPage() + "§8/§7" + (this.getUser().getMaxPage() == Integer.MAX_VALUE ? "X" : this.getUser().getMaxPage());
    }
}
