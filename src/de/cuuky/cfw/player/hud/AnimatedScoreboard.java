package de.cuuky.cfw.player.hud;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AnimatedScoreboard {

	private final StaticScoreboard scoreboard;
	private final int task;
	private Animation<String> title;
	private Animation<String[]> content;
	private boolean contentChanged;

	public AnimatedScoreboard(JavaPlugin javaPlugin, ScoreboardInstance scoreboard, AnimationData<String> title,
			ScoreboardAnimationData content) {
		this.title = new Animation<>(title);
		this.content = new Animation<>(content);

		this.scoreboard = new StaticScoreboard(scoreboard) {
			@Override
			protected String processString(String input) {
				return AnimatedScoreboard.this.processString(input);
			}
		};
		this.update();
		this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, this::update, 0, 0);
	}

	public void queueUpdate() {
		this.contentChanged = true;
	}

	private void update() {
		if (this.scoreboard.isEnabled()) {
			String title = null;
			String[] content = null;
			if (this.contentChanged || this.title.shouldUpdate())
				title = this.title.getCurrentFrame();

			if (this.contentChanged || this.content.shouldUpdate())
				content = this.content.getCurrentFrame();

			this.scoreboard.update(title, content);

			this.contentChanged = false;

			this.title.tick();
			this.content.tick();
		} else
			this.scoreboard.update(null, (String[]) null);
	}

	protected String processString(String input) {
		return input;
	}

	public void setTitle(AnimationData<String> title) {
		this.title = new Animation<>(title);
		this.contentChanged = true;
	}

	public void setContent(ScoreboardAnimationData content) {
		this.content = new Animation<>(content);
		this.contentChanged = true;
	}

	public void setEnabled(boolean enabled) {
		this.scoreboard.setEnabled(enabled);
		this.contentChanged = true;
	}

	public boolean isEnabled() {
		return this.scoreboard.isEnabled();
	}

	public void destroy() {
		Bukkit.getScheduler().cancelTask(this.task);
	}
}
