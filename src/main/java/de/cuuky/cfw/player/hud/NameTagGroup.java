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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class NameTagGroup {

	private List<PlayerNameTag> nameTags;

	public NameTagGroup() {
		this.nameTags = new ArrayList<>();
	}

	public void register(ScoreboardInstance scoreboard, boolean visible, String name, String prefix, String suffix) {
		PlayerNameTag nameTag = new PlayerNameTag(scoreboard);
		nameTag.setValues(visible, name, prefix, suffix);
		this.nameTags.add(nameTag);
		for (PlayerNameTag n : this.nameTags) {
			nameTag.updatePlayer(n);
			n.updatePlayer(nameTag);
		}
	}

	public void register(ScoreboardInstance scoreboard, boolean visible, String prefix, String suffix) {
		this.register(scoreboard, visible, scoreboard.getPlayer().getName(), prefix, suffix);
	}

	public void unRegister(ScoreboardInstance scoreboard) {
		this.nameTags.removeIf(nameTag -> nameTag.getScoreboard() == scoreboard);
	}

	public void unRegister(Player player) {
		this.nameTags.removeIf(nameTag -> nameTag.getScoreboard().getPlayer() == player);
	}

	public void update(Player player, boolean visible, String name, String prefix, String suffix) {
		for (PlayerNameTag nameTag : this.nameTags) {
			if (nameTag.getScoreboard().getPlayer() == player)
				nameTag.setValues(visible, name, prefix, suffix);
			nameTag.updatePlayer(player, visible, name, prefix, suffix);
		}
	}

	public void update(Player player, boolean visible, String prefix, String suffix) {
		this.update(player, visible, player.getName(), prefix, suffix);
	}

	public void update(ScoreboardInstance scoreboard, boolean visible, String name, String prefix, String suffix) {
		this.update(scoreboard.getPlayer(), visible, name, prefix, suffix);
	}

	public void update(ScoreboardInstance scoreboard, boolean visible, String prefix, String suffix) {
		this.update(scoreboard, visible, scoreboard.getPlayer().getName(), prefix, suffix);
	}
}
