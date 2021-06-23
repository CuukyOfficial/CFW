package de.cuuky.cfw.version;

import org.bukkit.entity.Player;

public class OneThirteenVersionAdapter extends OneTwelveVersionAdapter {

	@Override
	protected void initTablist() {
	}
	
	@Override
	public void sendTablist(Player player, String header, String footer) {
		player.setPlayerListHeaderFooter(header, footer);
	}
}
