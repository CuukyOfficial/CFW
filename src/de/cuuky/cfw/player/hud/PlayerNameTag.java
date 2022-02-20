package de.cuuky.cfw.player.hud;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.cuuky.cfw.version.VersionAdapter;
import de.cuuky.cfw.version.VersionUtils;

class PlayerNameTag {

	private static final VersionAdapter VERISON_ADAPTER = VersionUtils.getVersionAdapter();

	private ScoreboardInstance scoreboard;
	private String name;
	private String prefix;
	private String suffix;
	private boolean visible;

	PlayerNameTag(ScoreboardInstance scoreboard) {
		this.scoreboard = scoreboard;
	}

	void setValues(boolean visible, String name, String prefix, String suffix) {
		this.visible = visible;
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	void updatePlayer(Player player, boolean visible, String name, String prefix, String suffix) {
		Scoreboard scoreboard = this.scoreboard.getScoreboard();
		Team team = scoreboard.getEntryTeam(player.getName());

		if (team != null && !team.getName().equals(name)) {
			team.removeEntry(player.getName());
			team.unregister();
			team = null;
		}

		if (team == null) {
			team = scoreboard.registerNewTeam(name);
			team.addEntry(player.getName());
		}

		team.setPrefix(prefix);
		team.setSuffix(suffix);
		VERISON_ADAPTER.setNametagVisibility(team, visible);
	}

	void updatePlayer(PlayerNameTag nameTag) {
		this.updatePlayer(nameTag.scoreboard.getPlayer(), nameTag.visible, nameTag.name, nameTag.prefix, nameTag.suffix);
	}

	ScoreboardInstance getScoreboard() {
		return scoreboard;
	}
}
