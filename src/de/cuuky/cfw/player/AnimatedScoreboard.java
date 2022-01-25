package de.cuuky.cfw.player;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;

public class AnimatedScoreboard {

	private static final int TITLE_MAX = 32;
	private static final int CONTENT_MAX = 40;
	private static final int LINES_MAX = 15;

	private ScoreboardInstance scoreboard;
	private Animation<String> title;
	private Animation<String[]> content;
	private int task;
	private String[] buffer;
	private boolean enabled;
	private boolean enabledChanged;
	private boolean contentChanged;

	public AnimatedScoreboard(JavaPlugin javaPlugin, ScoreboardInstance scoreboard, AnimationData<String> title, ScoreboardAnimationData content) {
		this.scoreboard = scoreboard;
		this.title = new Animation<>(title);
		this.content = new Animation<>(content);
		this.buffer = new String[LINES_MAX];
		this.enabled = true;
		this.contentChanged = true;
		
		scoreboard.registerSideBar();

		this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, this::update, 0, 0);
	}

	public void queueUpdate() {
		this.contentChanged = true;
	}

	private void update() {
		if (this.enabledChanged) {
			if (!this.enabled) {
				this.scoreboard.clearSideBar();
				this.title.reset();
				this.content.reset();

				for (int i = 0; i < this.buffer.length; i++)
					this.buffer[i] = null;
			} else
				this.scoreboard.registerSideBar();

			this.enabledChanged = false;
		}

		if (this.enabled) {
			if (this.contentChanged || this.title.shouldUpdate())
				this.sendTitle();

			if (this.contentChanged || this.content.shouldUpdate())
				this.sendContent();

			this.contentChanged = false;

			this.title.tick();
			this.content.tick();
		}
	}

	private void sendTitle() {
		Objective objective = this.scoreboard.getSideBar();
		String title = this.limit(this.replacePlaceholders(this.title.getCurrentFrame()), TITLE_MAX);

		if (!objective.getDisplayName().equals(title))
			objective.setDisplayName(title);
	}

	private void sendContent() {
		Objective objective = this.scoreboard.getSideBar();
		String[] content = this.content.getCurrentFrame();

		for (int i = 0; i < LINES_MAX; i++) {
			String prevLine = this.buffer[i];

			if (i < content.length) {
				String line = this.limit(this.replacePlaceholders(content[i]), CONTENT_MAX);
				if (!line.equals(prevLine)) {
					this.checkReset(objective, i);
					objective.getScore(line).setScore(LINES_MAX - 1 - i);

					this.buffer[i] = line;
				}
			} else if (this.checkReset(objective, i))
				this.buffer[i] = null;
		}
	}

	private boolean checkReset(Objective objective, int i) {
		String line = this.buffer[i];
		if (line == null)
			return false;

		for (int j = 0; j < i; j++)
			if (line.equals(this.buffer[j]))
				return false;

		objective.getScoreboard().resetScores(line);
		return true;
	}

	protected String replacePlaceholders(String input) {
		return input;
	}

	private String limit(String string, int max) {
		if (string.length() > max)
			return string.substring(0, max);
		else
			return string;
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
		this.enabledChanged = enabled != this.enabled;
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void destroy() {
		Bukkit.getScheduler().cancelTask(this.task);
	}
}
