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

package de.cuuky.cfw.clientadapter.board.tablist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import de.cuuky.cfw.clientadapter.board.CustomBoard;
import de.cuuky.cfw.clientadapter.board.CustomBoardType;
import de.cuuky.cfw.player.CustomPlayer;
import de.cuuky.cfw.player.PlayerVersionAdapter;
import de.cuuky.cfw.utils.JavaUtils;
import de.cuuky.cfw.version.BukkitVersion;
import de.cuuky.cfw.version.VersionUtils;

public class CustomTablist<T extends CustomPlayer> extends CustomBoard<T> {

	private final PlayerVersionAdapter adapter;

	private Map<Player, List<String>> headerReplaces;
	private Map<Player, List<String>> footerReplaces;

	private String tabname;

	public CustomTablist(T player) {
		super(CustomBoardType.TABLIST, player);
		this.adapter = new PlayerVersionAdapter(player.getPlayer());
	}

	@Override
	protected void onEnable() {
		this.headerReplaces = new HashMap<>();
		this.footerReplaces = new HashMap<>();
	}

	private Object[] updateList(T player, boolean header) {
		List<String> tablistLines = header ? player.getUpdateHandler().getTablistHeader() : player.getUpdateHandler().getTablistFooter(), oldList = null;

		if (tablistLines == null)
			return new Object[] { false };

		if (header) {
			oldList = headerReplaces.get(player.getPlayer());
			if (oldList == null)
				headerReplaces.put(player.getPlayer(), oldList = new ArrayList<>());
		} else {
			oldList = footerReplaces.get(player.getPlayer());
			if (oldList == null)
				footerReplaces.put(player.getPlayer(), oldList = new ArrayList<>());
		}

		boolean changed = false;
		for (int index = 0; index < tablistLines.size(); index++) {
			String line = tablistLines.get(index);

			if (oldList.size() < tablistLines.size()) {
				oldList.add(line);
				changed = true;
			} else if (!oldList.get(index).equals(line)) {
				oldList.set(index, line);
				changed = true;
			}
		}

		return new Object[] { changed, oldList };
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onUpdate() {
		String tablistname = player.getUpdateHandler().getTablistName();

		if (tablistname != null) {
			int maxlength = BukkitVersion.ONE_8.isHigherThan(VersionUtils.getVersion()) ? 16 : -1;
			if (maxlength > 0)
				if (tablistname.length() > maxlength)
					tablistname = this.player.getName();

			if (this.tabname == null || !this.tabname.equals(tablistname))
				player.getPlayer().setPlayerListName(this.tabname = tablistname);
		}

		Object[] headerUpdate = updateList(player, true);
		Object[] footerUpdate = updateList(player, false);

		if ((boolean) headerUpdate[0] || (boolean) footerUpdate[0])
			this.adapter.sendTablist(JavaUtils.getArgsToString((ArrayList<String>) headerUpdate[1], "\n"), JavaUtils.getArgsToString((ArrayList<String>) footerUpdate[1], "\n"));
	}
}