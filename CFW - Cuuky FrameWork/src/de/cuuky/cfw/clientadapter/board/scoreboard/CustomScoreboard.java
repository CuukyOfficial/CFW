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
	private String title;

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
		String name = getAsTeam(index);

		int firstEndIndex = 16;
		String line16Char = line.substring(0, line.length() > firstEndIndex ? firstEndIndex : line.length());
		if (line.length() > firstEndIndex) {
			String lastColor = "";

			if (line16Char.endsWith("§")) {
				lastColor = ChatColor.getLastColors("§" + line.charAt(firstEndIndex));

				if (!lastColor.isEmpty()) {
					line = line.substring(0, firstEndIndex - 1) + line.substring(firstEndIndex + 1);
					firstEndIndex = 15;
				}
			}

			if (lastColor.isEmpty())
				lastColor = ChatColor.getLastColors(line16Char);

			name = name + (!lastColor.isEmpty() ? lastColor : "§f");
		}

		Team team = board.getTeam("team-" + name);
		if (team == null)
			team = board.registerNewTeam("team-" + name);

		team.setPrefix(line.substring(0, line.length() < firstEndIndex ? line.length() : firstEndIndex));
		
		int suffixLength = (line.length() > firstEndIndex + 16 ? firstEndIndex + 16 : line.length());
		team.setSuffix(line.length() > firstEndIndex ? line.substring(firstEndIndex, suffixLength) : "");
		team.addPlayer(new FakeOfflinePlayer(name));

		return name;
	}

	private String getAsTeam(int index) {
		return ChatColor.values()[index].toString();
	}

	@Override
	protected void onUpdate() {
		ArrayList<String> scoreboardLines = this.getUpdateHandler().getScoreboardEntries(this.player.getPlayer());
		Scoreboard board = player.getPlayer().getScoreboard();
		Objective objective = board.getObjective(DisplaySlot.SIDEBAR);

		if (objective == null || this.title == null || !this.title.equals(objective.getDisplayName())) {
			sendScoreBoard();
			return;
		}

		if (board.getEntries().size() > scoreboardLines.size()) {
			for (Team team : board.getTeams())
				if (team.getName().startsWith("team-"))
					team.unregister();

			for (String s : board.getEntries())
				board.resetScores(s);

			replaces = new ArrayList<>();
		}

		for (int index = 0; index < scoreboardLines.size(); index++) {
			String line = scoreboardLines.get(index);

			if (replaces.size() < scoreboardLines.size()) {
				objective.getScore(prepareScoreboardStatement(index, line)).setScore(index + 1);
				replaces.add(line);
			} else if (!replaces.get(index).equals(line)) {
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
		this.title = this.getUpdateHandler().getScoreboardTitle(this.player.getPlayer());

		if (this.title.length() >= 32)
			this.title = this.title.substring(0, 32);

		obj.setDisplayName(this.title);

		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.getPlayer().setScoreboard(sb);

		replaces = new ArrayList<>();
		update();
	}
}