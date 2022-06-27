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

package de.varoplugin.cfw.player.hud;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

class DefaultScoreboardInstance implements ScoreboardInstance {

    private final Player player;
    private final Scoreboard scoreboard;

    DefaultScoreboardInstance(Player player) {
        this.player = player;
        this.scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        player.setScoreboard(this.scoreboard);
    }

    @Override
    public Objective registerSideBar() {
        @SuppressWarnings("deprecation")
        Objective objective = this.scoreboard.registerNewObjective("CFW", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        return objective;
    }

    @Override
    public Objective getSideBar() {
        return this.scoreboard.getObjective(DisplaySlot.SIDEBAR);
    }

    @Override
    public void clearSideBar() {
        this.getSideBar().unregister();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
