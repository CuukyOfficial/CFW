package de.cuuky.cfw.player.hud;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class NameTagGroup {

	private List<PlayerNameTag> nameTags;

	public NameTagGroup() {
		this.nameTags = new ArrayList<>();
	}

	public void register(ScoreboardInstance scoreboard, boolean visible, String prefix, String suffix) {
		PlayerNameTag nameTag = new PlayerNameTag(scoreboard);
		nameTag.setValues(visible, prefix, suffix);
		this.nameTags.add(nameTag);
		for (PlayerNameTag n : this.nameTags) {
			nameTag.updatePlayer(n);
			n.updatePlayer(nameTag);
		}
	}

	public void unRegister(ScoreboardInstance scoreboard) {
		this.nameTags.removeIf(nameTag -> nameTag.getScoreboard() == scoreboard);
	}

	public void unRegister(Player player) {
		this.nameTags.removeIf(nameTag -> nameTag.getScoreboard().getPlayer() == player);
	}

	public void update(Player player, boolean visible, String prefix, String suffix) {
		for (PlayerNameTag nameTag : this.nameTags) {
			if (nameTag.getScoreboard().getPlayer() == player)
				nameTag.setValues(visible, prefix, suffix);
			nameTag.updatePlayer(player, visible, prefix, suffix);
		}
	}

	public void update(ScoreboardInstance scoreboard, boolean visible, String prefix, String suffix) {
		this.update(scoreboard.getPlayer(), visible, prefix, suffix);
	}
}
