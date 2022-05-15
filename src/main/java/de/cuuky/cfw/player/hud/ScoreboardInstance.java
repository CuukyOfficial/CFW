package de.cuuky.cfw.player.hud;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public interface ScoreboardInstance {

	Objective registerSideBar();

	Objective getSideBar();

	void clearSideBar();

	Player getPlayer();

	Scoreboard getScoreboard();

	public static ScoreboardInstance newInstance(Player player) {
		return new DefaultScoreboardInstance(player);
	}
}