package de.cuuky.cfw.version;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

class OneNineVersionAdapter extends OneEightVersionAdapter {

	@Override
	protected void initNbt() throws ClassNotFoundException, NoSuchMethodException, SecurityException {}
	
	@Override
	public void setAttributeSpeed(Player player, double value) {
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(value);
	}

	@Override
	public void setNametagVisibility(Team team, boolean shown) {
		team.setOption(Team.Option.NAME_TAG_VISIBILITY, shown ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER);
	}

	@Override
	public void removeAi(LivingEntity entity) {
		entity.setAI(false);
	}
}
