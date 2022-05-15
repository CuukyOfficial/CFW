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

package de.cuuky.cfw.inventory;

import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.cuuky.cfw.inventory.inserter.DirectInserter;
import de.cuuky.cfw.utils.item.BuildItem;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.cfw.version.types.Sounds;

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