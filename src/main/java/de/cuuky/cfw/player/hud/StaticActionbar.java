package de.cuuky.cfw.player.hud;

import org.bukkit.entity.Player;

import de.cuuky.cfw.player.CustomPlayer;
import de.cuuky.cfw.version.VersionAdapter;
import de.cuuky.cfw.version.VersionUtils;

public class StaticActionbar {
	
	private static final VersionAdapter VERSION_ADAPTER = VersionUtils.getVersionAdapter();

	private final Player player;
	private boolean enabled;

	public StaticActionbar(Player player) {
		this.player = player;
		this.enabled = true;
	}
	
	public StaticActionbar(CustomPlayer player) {
		this(player.getPlayer());
	}

	public void update(String content) {
		VERSION_ADAPTER.sendActionbar(this.player.getPlayer(), this.isEnabled() ? this.processString(content) : "");
	}

	protected String processString(String input) {
		return input;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
