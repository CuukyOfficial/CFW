package de.cuuky.cfw.clientadapter.board;

import de.cuuky.cfw.clientadapter.board.nametag.CustomNametag;
import de.cuuky.cfw.clientadapter.board.scoreboard.CustomScoreboard;
import de.cuuky.cfw.clientadapter.board.tablist.CustomTablist;
import de.cuuky.cfw.player.CustomPlayer;

public enum CustomBoardType {

	NAMETAG(CustomNametag.class),
	SCOREBOARD(CustomScoreboard.class),
	TABLIST(CustomTablist.class);

	private Class<? extends CustomBoard<? extends CustomPlayer>> clazz;

	private <B extends CustomBoard<? extends CustomPlayer>> CustomBoardType(Class<B> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends CustomBoard<? extends CustomPlayer>> getTypeClass() {
		return clazz;
	}
}