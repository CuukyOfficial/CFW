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

import java.util.List;
import java.util.UUID;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface SkullBuilder extends UnboundItemBuilder {

    ItemStack build(UUID uuid);

    ItemStack build(Player player);

    @Override
    SkullBuilder amount(int amount);

    @Override
    SkullBuilder addEnchantment(Enchantment enchantment, int amplifier);

    @Override
    SkullBuilder deleteDamageAnnotation(boolean deleteAnnotations);

    @Override
    SkullBuilder deleteDamageAnnotation();

    @Override
    SkullBuilder displayName(String displayName);

    @Override
    SkullBuilder addLore(String add);

    @Override
    SkullBuilder lore(List<String> lore);

    @Override
    SkullBuilder lore(String lore);

    @Override
    SkullBuilder lore(String... lore);
}
