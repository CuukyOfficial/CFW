package de.cuuky.cfw.clientadapter.board.scoreboard;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.cuuky.cfw.clientadapter.board.CustomBoard;
import de.cuuky.cfw.clientadapter.board.CustomBoardType;
import de.cuuky.cfw.player.CustomPlayer;

public final class CustomScoreboard extends CustomBoard {

	private ArrayList<String> replaces;

	public CustomScoreboard(CustomPlayer player) {
		super(CustomBoardType.SCOREBOARD, player);
	}

	@Override
	protected void onEnable() {
		replaces = new ArrayList<>();
	}

	@SuppressWarnings("deprecation")
	private String prepareScoreboardStatement(int index, String line) {
		Scoreboard board = player.getPlayer().getScoreboard();
		Team team = board.getTeam("team-" + getAsTeam(index));
		if(team == null)
			team = board.registerNewTeam("team-" + getAsTeam(index));

		String playerName = getAsTeam(index);
		String[] pas = getPrefixAndSuffix(line);
		team.setPrefix(pas[0]);
		team.setSuffix(pas[1]);
		team.addPlayer(new FakeOfflinePlayer(playerName));

		return playerName;
	}

	private String[] getPrefixAndSuffix(String value) {
		String prefix = getPrefix(value);
		String suffix = "";

		if(prefix.substring(prefix.length() - 1, prefix.length()).equals("§")) {
			prefix = prefix.substring(0, prefix.length() - 1);
			suffix = getPrefix("§" + getSuffix(value));
		} else
			suffix = getPrefix(ChatColor.getLastColors(prefix) + getSuffix(value));

		return new String[] { prefix, suffix };
	}

	private String getPrefix(String value) {
		return value.length() > 16 ? value.substring(0, 16) : value;
	}

	private String getSuffix(String value) {
		value = value.length() > 32 ? value.substring(0, 32) : value;

		return value.length() > 16 ? value.substring(16) : "";
	}

	private String getAsTeam(int index) {
		return ChatColor.values()[index].toString();
	}

	private void updateHeartBar() {
		if(!player.getPlayer().getScoreboard().getObjectives().stream().noneMatch(objective -> objective.getName().equals("name")))
			return;

		Objective healthName = player.getPlayer().getScoreboard().registerNewObjective("name", "health");
		healthName.setDisplaySlot(DisplaySlot.BELOW_NAME);
		healthName.setDisplayName(String.valueOf(ChatColor.DARK_RED));
	}

	@Override
	protected void onUpdate() {
		ArrayList<String> scoreboardLines = this.getUpdateHandler().getScoreboardEntries(this.player.getPlayer());
		Scoreboard board = player.getPlayer().getScoreboard();
		Objective objective = board.getObjective(DisplaySlot.SIDEBAR);

		if(objective == null) {
			sendScoreBoard();
			return;
		}

		if(this.getUpdateHandler().showHeartsBelowName(player.getPlayer()))
			updateHeartBar();

		if(board.getEntries().size() > scoreboardLines.size()) {
			for(Team team : board.getTeams())
				if(team.getName().startsWith("team-"))
					team.unregister();

			for(String s : board.getEntries())
				board.resetScores(s);

			replaces = new ArrayList<>();
		}

		for(int index = 0; index < scoreboardLines.size(); index++) {
			String line = scoreboardLines.get(index);

			if(replaces.size() < scoreboardLines.size()) {
				objective.getScore(prepareScoreboardStatement(index, line)).setScore(index + 1);
				replaces.add(line);
			} else if(!replaces.get(index).equals(line)) {
				String sbs = prepareScoreboardStatement(index, line);
				board.resetScores(sbs);
				objective.getScore(sbs).setScore(index + 1);
				replaces.set(index, line);
			}
		}
	}

	public void sendScoreBoard() {
		Scoreboard sb = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		Objective obj = sb.registerNewObjective("silent", "dummy");
		obj.setDisplayName(this.getUpdateHandler().getScoreboardTitle(this.player.getPlayer()));

		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.getPlayer().setScoreboard(sb);

		replaces = new ArrayList<>();
		update();
	}
}