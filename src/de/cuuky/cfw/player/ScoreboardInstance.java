package de.cuuky.cfw.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardInstance {

	private Player player;
	private Scoreboard scoreboard;

	public ScoreboardInstance(Player player) {
		this.player = player;
		this.scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		player.setScoreboard(scoreboard);
	}

	public Objective registerSideBar() {
		@SuppressWarnings("deprecation")
		Objective objective = scoreboard.registerNewObjective("CFW", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		return objective;
	}

	public Objective getSideBar() {
		return this.scoreboard.getObjective(DisplaySlot.SIDEBAR);
	}

	public void clearSideBar() {
		this.getSideBar().unregister();
	}

	public Player getPlayer() {
		return player;
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}
}
