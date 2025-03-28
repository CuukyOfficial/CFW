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

package de.varoplugin.cfw.version;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import com.cryptomorin.xseries.XAttribute;

class OneNineVersionAdapter extends OneEightVersionAdapter {

    @Override
    protected void initNbt() throws SecurityException {
        // nop
    }

    @Override
    public void setAttributeSpeed(Player player, double value) {
        player.getAttribute(XAttribute.ATTACK_SPEED.get()).setBaseValue(value);
    }

    @Override
    public void setNameTagVisibility(Team team, boolean shown) {
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, shown ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER);
    }

    @Override
    public ItemStack getItemInUse(Player player) {
        return player.getItemInUse();
    }

    @Override
    public void removeAi(LivingEntity entity) {
        entity.setAI(false);
    }
}
