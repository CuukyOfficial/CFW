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

package de.varoplugin.cfw.item;

import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.cryptomorin.xseries.XMaterial;

class SkullBuilderImpl extends ItemBuilderImpl {

    private final Consumer<SkullMeta> callback;

    SkullBuilderImpl(Consumer<SkullMeta> callback) {
        super(XMaterial.PLAYER_HEAD);
        this.callback = callback;
    }

    @Override
    public ItemStack build() {
        ItemStack itemStack = super.build();
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        this.callback.accept(meta);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    static SkullBuilderImpl uuid(UUID uuid) {
        return new SkullBuilderImpl(meta -> meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid)));
    }

    @SuppressWarnings("deprecation")
    static SkullBuilderImpl name(String name) {
        return new SkullBuilderImpl(meta -> meta.setOwner(name));
    }
}
