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

package de.cuuky.cfw.clientadapter.board.nametag;

import de.cuuky.cfw.clientadapter.board.CustomBoard;
import de.cuuky.cfw.clientadapter.board.CustomBoardType;
import de.cuuky.cfw.player.CustomPlayer;
import de.cuuky.cfw.version.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;

@Deprecated
public class CustomNametag<T extends CustomPlayer> extends CustomBoard<T> {

	// 0 = name, 1 = prefix, 2 = suffix
	private String[] nametagContent = new String[3];
	private String oldName;
	private boolean initalized, nametagShown = true;

	public CustomNametag(T player) {
		super(CustomBoardType.NAMETAG, player);
	}

	@Override
	public void onEnable() {
		Scoreboard sb = player.getPlayer().getScoreboard();
		if (sb.getTeams().size() > 0)
			for (int i = sb.getTeams().size() - 1; i != 0; i--) {
				Team team = (Team) sb.getTeams().toArray()[i];
				try {
					if (!team.getName().startsWith("team-"))
						team.unregister();
				} catch (IllegalStateException e) {}
			}

		giveAll();
		this.initalized = true;
	}

	public void startDelayedRefresh() {
		new BukkitRunnable() {
			@Override
			public void run() {
				update();
			}
		}.runTaskLater(this.manager.getOwnerInstance(), 1L);
	}

	private boolean refreshPrefix() {
		String newName = this.player.getUpdateHandler().getNametagName();
		if (newName != null && newName.startsWith("team-"))
			throw new IllegalArgumentException("Player nametag name cannot start with 'team-'");

		String[] check = new String[] { newName, this.player.getUpdateHandler().getNametagPrefix(), this.player.getUpdateHandler().getNametagSuffix() };
		for (int i = 0; i < check.length; i++) {
			String newContent = check[i];
			if (newContent == null)
				continue;

			if (newContent.length() > 16)
				check[i] = newContent.substring(0, 16);
		}

		boolean showNametag = this.player.getUpdateHandler().isNametagVisible();
		boolean changed = !Arrays.equals(this.nametagContent, check) || showNametag != this.nametagShown;
		if (!changed)
			return false;

		this.oldName = nametagContent[0];
		this.nametagContent = check;
		this.nametagShown = showNametag;
		return true;
	}

	private void updateFor(Scoreboard board, CustomNametag<T> nametag) {
		Team oldTeam = board.getTeam(nametag.getOldName() != null ? nametag.getOldName() : this.player.getPlayer().getName());
		if (oldTeam != null)
			oldTeam.unregister();

		String teamName = nametag.getName() == null ? this.player.getPlayer().getName() : nametag.getName();
		Team team = board.getTeam(teamName);
		if (team == null) {
			team = board.registerNewTeam(teamName);
			team.addPlayer(nametag.getPlayer().getPlayer());
		}

		setVisibility(team, nametag.isNametagShown());
		if (nametag.getPrefix() != null) {
			if (team.getPrefix() == null)
				team.setPrefix(nametag.getPrefix());
			else if (!team.getPrefix().equals(nametag.getPrefix()))
				team.setPrefix(nametag.getPrefix());
		} else
			team.setPrefix(null);

		if (nametag.getSuffix() != null) {
			if (team.getSuffix() == null)
				team.setSuffix(nametag.getSuffix());
			else if (!team.getSuffix().equals(nametag.getSuffix()))
				team.setSuffix(nametag.getSuffix());
		} else
			team.setSuffix(null);
	}

	@Override
	protected void onUpdate() {
		if (!refreshPrefix())
			return;

		setToAll();
	}

	public void giveAll() {
		Scoreboard board = player.getPlayer().getScoreboard();
		for (CustomBoard<T> nametag : this.manager.getBoards(CustomBoardType.NAMETAG))
			if (((CustomNametag<T>) nametag).isInitalized())
				updateFor(board, (CustomNametag<T>) nametag);
	}

	public void setToAll() {
		for (Player toSet : VersionUtils.getVersionAdapter().getOnlinePlayers())
			updateFor(toSet.getScoreboard(), this);
	}

	public String getPrefix() {
		return this.nametagContent[1];
	}

	public String getName() {
		return this.nametagContent[0];
	}

	public String getSuffix() {
		return this.nametagContent[2];
	}

	public String getOldName() {
		return oldName;
	}

	public boolean isNametagShown() {
		return nametagShown;
	}

	public boolean isInitalized() {
		return initalized;
	}

	private static void setVisibility(Team team, boolean shown) {
		VersionUtils.getVersionAdapter().setNametagVisibility(team, shown);
	}
}