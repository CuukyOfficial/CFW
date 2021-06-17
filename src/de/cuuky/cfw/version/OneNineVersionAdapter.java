package de.cuuky.cfw.version;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

class OneNineVersionAdapter extends OneEightVersionAdapter {

	@Override
	public void setAttributeSpeed(Player player, double value) {
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(value);
	}
}
