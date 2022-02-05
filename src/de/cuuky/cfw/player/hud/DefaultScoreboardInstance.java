package de.cuuky.cfw.player.hud;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

class DefaultScoreboardInstance implements ScoreboardInstance {

	private Player player;
	private Scoreboard scoreboard;

	DefaultScoreboardInstance(Player player) {
		this.player = player;
		this.scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		player.setScoreboard(this.scoreboard);
	}

	@Override
	public Objective registerSideBar() {
		@SuppressWarnings("deprecation")
		Objective objective = this.scoreboard.registerNewObjective("CFW", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		return objective;
	}

	@Override
	public Objective getSideBar() {
		return this.scoreboard.getObjective(DisplaySlot.SIDEBAR);
	}

	@Override
	public void clearSideBar() {
		this.getSideBar().unregister();
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
}
