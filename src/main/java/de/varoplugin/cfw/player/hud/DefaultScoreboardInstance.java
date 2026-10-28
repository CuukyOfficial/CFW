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
import org.bukkit.scoreboard.Team;

import de.varoplugin.cfw.version.ServerVersion;
import de.varoplugin.cfw.version.VersionAdapter;
import de.varoplugin.cfw.version.VersionUtils;

class DefaultScoreboardInstance implements ScoreboardInstance {

    private static final VersionAdapter VERSION_ADAPTER = VersionUtils.getVersionAdapter();
    private static final boolean ONE_SEVEN = VersionUtils.getVersion().isLowerThan(ServerVersion.VERSION_1_8);

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
    public void setSideBarTitle(String title) {
        Objective objective = this.getSideBar();
        if (!objective.getDisplayName().equals(title))
            objective.setDisplayName(title);
    }

    @Override
    public void setSideBarScore(String line, int score) {
        this.getSideBar().getScore(line).setScore(score);
    }

    @Override
    public void resetSideBarScore(String line) {
        this.scoreboard.resetScores(line);
    }

    @Override
    public void setNameTag(Player target, boolean visible, String teamName, String prefix, String suffix) {
        @SuppressWarnings("deprecation")
        Team team = ONE_SEVEN ? this.scoreboard.getPlayerTeam(target) : this.scoreboard.getEntryTeam(target.getName());

        if (team != null && !team.getName().equals(teamName)) {
            team.removeEntry(target.getName());
            team.unregister();
            team = null;
        }

        if (team == null) {
            if ((team = this.scoreboard.getTeam(teamName)) == null)
                team = this.scoreboard.registerNewTeam(teamName);
            team.addEntry(target.getName());
        }

        team.setPrefix(prefix);
        team.setSuffix(suffix);
        VERSION_ADAPTER.setNameTagVisibility(team, visible);
    }

    @Override
    public void destroy() {
        // The Bukkit scoreboard is simply discarded together with the player
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
}
