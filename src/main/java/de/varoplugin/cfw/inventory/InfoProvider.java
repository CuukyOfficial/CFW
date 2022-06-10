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

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
