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

package de.cuuky.cfw.player.hud;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.cuuky.cfw.version.VersionAdapter;
import de.cuuky.cfw.version.VersionUtils;

class PlayerNameTag {

	private static final VersionAdapter VERISON_ADAPTER = VersionUtils.getVersionAdapter();

	private ScoreboardInstance scoreboard;
	private String name;
	private String prefix;
	private String suffix;
	private boolean visible;

	PlayerNameTag(ScoreboardInstance scoreboard) {
		this.scoreboard = scoreboard;
	}

	void setValues(boolean visible, String name, String prefix, String suffix) {
		this.visible = visible;
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	void updatePlayer(Player player, boolean visible, String name, String prefix, String suffix) {
		Scoreboard scoreboard = this.scoreboard.getScoreboard();
		Team team = scoreboard.getEntryTeam(player.getName());

		if (team != null && !team.getName().equals(name)) {
			team.removeEntry(player.getName());
			team.unregister();
			team = null;
		}

		if (team == null) {
			team = scoreboard.registerNewTeam(name);
			team.addEntry(player.getName());
		}

		team.setPrefix(prefix);
		team.setSuffix(suffix);
		VERISON_ADAPTER.setNametagVisibility(team, visible);
	}

	void updatePlayer(PlayerNameTag nameTag) {
		this.updatePlayer(nameTag.scoreboard.getPlayer(), nameTag.visible, nameTag.name, nameTag.prefix, nameTag.suffix);
	}

	ScoreboardInstance getScoreboard() {
		return scoreboard;
	}
}
