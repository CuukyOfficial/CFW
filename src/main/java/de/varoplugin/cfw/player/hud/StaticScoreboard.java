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

import org.bukkit.scoreboard.Objective;

import de.varoplugin.cfw.version.BukkitVersion;
import de.varoplugin.cfw.version.VersionUtils;

import java.util.Arrays;

public class StaticScoreboard {

    private static final int TITLE_MAX = 32;
    private static final int CONTENT_MAX = VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_7) ? 40 : 16;
    private static final int LINES_MAX = 15;

    private final ScoreboardInstance scoreboard;
    private final String[] buffer;
    private boolean enabledChanged;
    private boolean enabled;

    public StaticScoreboard(ScoreboardInstance scoreboard, String title, String... content) {
        this.scoreboard = scoreboard;
        this.buffer = new String[LINES_MAX];
        this.enabled = true;
        this.enabledChanged = false;

        scoreboard.registerSideBar();

        this.update(title, content);
    }

    public StaticScoreboard(ScoreboardInstance scoreboard) {
        this(scoreboard, null, (String[]) null);
    }

    /**
     * Updates the scoreboard
     * 
     * @param title   The title (will not be updated if null)
     * @param content The title (will not be updated if null)
     */
    public void update(String title, String... content) {
        if (this.enabledChanged) {
            if (!this.enabled) {
                this.scoreboard.clearSideBar();

                // clear buffer
                Arrays.fill(this.buffer, null);
            } else
                this.scoreboard.registerSideBar();

            this.enabledChanged = false;
        }

        if (this.enabled) {
            if (title != null)
                this.sendTitle(title);
            if (content != null)
                this.sendContent(content);
        }
    }

    private void sendTitle(String title) {
        Objective objective = this.scoreboard.getSideBar();
        String realTitle = this.limit(this.processString(title), TITLE_MAX);

        if (!objective.getDisplayName().equals(realTitle))
            objective.setDisplayName(realTitle);
    }

    private void sendContent(String[] content) {
        Objective objective = this.scoreboard.getSideBar();

        for (int i = 0; i < LINES_MAX; i++) {
            String prevLine = this.buffer[i];

            if (i < content.length) {
                String line = this.limit(this.checkDuplicate(this.processString(content[i]), i), CONTENT_MAX);
                if (!line.equals(prevLine)) {
                    this.checkReset(objective, i);
                    objective.getScore(line).setScore(LINES_MAX - 1 - i);

                    this.buffer[i] = line;
                }
            } else if (this.checkReset(objective, i))
                this.buffer[i] = null;
        }
    }

    private boolean checkReset(Objective objective, int i) {
        String line = this.buffer[i];
        if (line == null)
            return false;

        for (int j = 0; j < i; j++)
            if (line.equals(this.buffer[j]))
                return false;

        objective.getScoreboard().resetScores(line);
        return true;
    }

    private String checkDuplicate(String input, int i) {
        for (int j = 0; j < i; j++)
            if (input.equals(this.buffer[j]))
                return checkDuplicate(input += "§r", i);
        return input;
    }

    private String limit(String string, int max) {
        if (string.length() > max)
            return string.substring(0, max);
        else
            return string;
    }

    protected String processString(String input) {
        return input;
    }

    /**
     * Enables or disables the scoreboard. {@link StaticScoreboard#update} has to be invoked for the change to take affect
     * 
     * @param enabled True if enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabledChanged = enabled != this.enabled;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
