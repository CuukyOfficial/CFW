package de.cuuky.cfw.player.hud;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AnimatedActionbar {

	private final StaticActionbar actionbar;
	private final int task;
	private Animation<String> content;
	private boolean contentChanged;
	
	public AnimatedActionbar(JavaPlugin javaPlugin, Player player, AnimationData<String> content) {
		this.content = new Animation<>(content);
		this.actionbar = new StaticActionbar(player) {
			@Override
			protected String processString(String input) {
				return AnimatedActionbar.this.processString(input);
			}
		};
		this.update();
		this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, this::update, 0, 0);
	}
	
	public void queueUpdate() {
		this.contentChanged = true;
	}

	private void update() {
		if(this.contentChanged || this.content.shouldUpdate())
			this.actionbar.update(this.content.getCurrentFrame());
		this.content.tick();
	}

	protected String processString(String input) {
		return input;
	}
	
	public boolean isEnabled() {
		return this.actionbar.isEnabled();
	}

	public void setEnabled(boolean enabled) {
		this.actionbar.setEnabled(enabled);
		this.contentChanged = true;
	}

	public void destroy() {
		Bukkit.getScheduler().cancelTask(this.task);
	}
}
