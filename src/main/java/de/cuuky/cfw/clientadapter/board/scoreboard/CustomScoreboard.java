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

package de.cuuky.cfw.clientadapter.board.scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.cuuky.cfw.clientadapter.board.CustomBoard;
import de.cuuky.cfw.clientadapter.board.CustomBoardType;
import de.cuuky.cfw.player.CustomPlayer;

@Deprecated
public final class CustomScoreboard<T extends CustomPlayer> extends CustomBoard<T> {

    private List<String> replaces;
    private String title;

    public CustomScoreboard(T player) {
        super(CustomBoardType.SCOREBOARD, player);
    }

    @Override
    protected void onEnable() {
        this.replaces = new ArrayList<>();
    }

    private String prepareScoreboardStatement(int index, String line) {
        Scoreboard board = player.getPlayer().getScoreboard();
        String name = getAsTeam(index);

        int firstEndIndex = 16;
        String line16Char = line.substring(0, line.length() > firstEndIndex ? firstEndIndex : line.length());
        if (line.length() > firstEndIndex) {
            String lastColor = "";

            if (line16Char.endsWith("§")) {
                lastColor = ChatColor.getLastColors("§" + line.charAt(firstEndIndex));

                if (!lastColor.isEmpty()) {
                    line = line.substring(0, firstEndIndex - 1) + line.substring(firstEndIndex + 1);
                    firstEndIndex = 15;
                }
            }

            if (lastColor.isEmpty())
                lastColor = ChatColor.getLastColors(line16Char);

            name = name + (!lastColor.isEmpty() ? lastColor : "§f");
        }

        Team team = board.getTeam("team-" + name);
        if (team == null)
            team = board.registerNewTeam("team-" + name);

        team.setPrefix(line.substring(0, line.length() < firstEndIndex ? line.length() : firstEndIndex));

        int suffixLength = (line.length() > firstEndIndex + 16 ? firstEndIndex + 16 : line.length());
        team.setSuffix(line.length() > firstEndIndex ? line.substring(firstEndIndex, suffixLength) : "");
        team.addPlayer(new FakeOfflinePlayer(name));

        return name;
    }

    private String getAsTeam(int index) {
        return ChatColor.values()[index].toString();
    }

    @Override
    protected void onUpdate() {
        ArrayList<String> scoreboardLines = this.player.getUpdateHandler().getScoreboardEntries();
        if (scoreboardLines == null)
            scoreboardLines = new ArrayList<>();

        Collections.reverse(scoreboardLines);

        Scoreboard board = player.getPlayer().getScoreboard();
        Objective objective = board.getObjective(DisplaySlot.SIDEBAR);

        if (objective == null || this.title == null || !this.title.equals(objective.getDisplayName())) {
            sendScoreBoard();
            return;
        }

        if (board.getEntries().size() > scoreboardLines.size()) {
            for (Team team : board.getTeams())
                if (team.getName().startsWith("team-"))
                    team.unregister();

            for (String s : board.getEntries())
                board.resetScores(s);

            replaces = new ArrayList<>();
        }

        for (int index = 0; index < scoreboardLines.size(); index++) {
            String line = scoreboardLines.get(index);

            if (replaces.size() < scoreboardLines.size()) {
                objective.getScore(prepareScoreboardStatement(index, line)).setScore(index + 1);
                replaces.add(line);
            } else if (!replaces.get(index).equals(line)) {
                String sbs = prepareScoreboardStatement(index, line);
                board.resetScores(sbs);
                objective.getScore(sbs).setScore(index + 1);
                replaces.set(index, line);
            }
        }
    }

    public void sendScoreBoard() {
        Scoreboard sb = this.manager.getOwnerInstance().getServer().getScoreboardManager().getNewScoreboard();
        Objective objective = sb.registerNewObjective("CFW", "dummy");
        this.title = this.player.getUpdateHandler().getScoreboardTitle();
        if (this.title == null)
            this.title = "";

        if (this.title.length() >= 32)
            this.title = this.title.substring(0, 32);

        objective.setDisplayName(this.title);
        this.title = objective.getDisplayName();
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.getPlayer().setScoreboard(sb);

        replaces = new ArrayList<>();
        update();
    }
}
