package de.cuuky.cfw.clientadapter.board.tablist;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

import de.cuuky.cfw.clientadapter.board.CustomBoard;
import de.cuuky.cfw.clientadapter.board.CustomBoardType;
import de.cuuky.cfw.player.CustomPlayer;
import de.cuuky.cfw.utils.JavaUtils;

public class CustomTablist extends CustomBoard {

	private HashMap<Player, ArrayList<String>> headerReplaces;

	private HashMap<Player, ArrayList<String>> footerReplaces;

	public CustomTablist(CustomPlayer player) {
		super(CustomBoardType.TABLIST, player);
	}

	@Override
	protected void onEnable() {
		this.headerReplaces = new HashMap<>();
		this.footerReplaces = new HashMap<>();
	}

	private Object[] updateList(CustomPlayer player, boolean header) {
		ArrayList<String> tablistLines = header ? this.getUpdateHandler().getTablistHeader(player.getPlayer()) : this.getUpdateHandler().getTablistFooter(player.getPlayer()), oldList = null;

		if(header) {
			oldList = headerReplaces.get(player.getPlayer());
			if(oldList == null)
				headerReplaces.put(player.getPlayer(), oldList = new ArrayList<>());
		} else {
			oldList = footerReplaces.get(player.getPlayer());
			if(oldList == null)
				footerReplaces.put(player.getPlayer(), oldList = new ArrayList<>());
		}

		boolean changed = false;
		for(int index = 0; index < tablistLines.size(); index++) {
			String line = tablistLines.get(index);

			if(oldList.size() < tablistLines.size()) {
				oldList.add(line);
				changed = true;
			} else if(!oldList.get(index).equals(line)) {
				oldList.set(index, line);
				changed = true;
			}
		}

		return new Object[] { changed, oldList };
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onUpdate() {
		Object[] headerUpdate = updateList(player, true);
		Object[] footerUpdate = updateList(player, false);

		if((boolean) headerUpdate[0] || (boolean) footerUpdate[0])
			player.getNetworkManager().sendTablist(JavaUtils.getArgsToString((ArrayList<String>) headerUpdate[1], "\n"), JavaUtils.getArgsToString((ArrayList<String>) footerUpdate[1], "\n"));
	}
}