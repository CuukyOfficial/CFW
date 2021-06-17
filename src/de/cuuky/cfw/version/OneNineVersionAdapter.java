package de.cuuky.cfw.version;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

class OneNineVersionAdapter extends OneEightVersionAdapter {

	@Override
	public void setAttributeSpeed(Player player, double value) {
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(value);
	}
	
	@Override
	public void setNametagVisibility(Team team, boolean shown) {
		team.setOption(Team.Option.NAME_TAG_VISIBILITY, shown ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER);
	}
}
