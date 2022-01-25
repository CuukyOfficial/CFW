package de.cuuky.cfw.player;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.version.VersionAdapter;
import de.cuuky.cfw.version.VersionUtils;

public class AnimatedTablist {

	private static final VersionAdapter VERISON_ADAPTER = VersionUtils.getVersionAdapter();

	private CustomPlayer player;
	private Animation<String> header;
	private Animation<String> footer;
	private int task;
	private String headerBuffer;
	private String footerBuffer;
	private boolean headerEnabled;
	private boolean footerEnabled;
	private boolean contentChanged;

	public AnimatedTablist(JavaPlugin javaPlugin, CustomPlayer player, AnimationData<String> header, AnimationData<String> footer) {
		this.player = player;
		this.header = new Animation<>(header);
		this.footer = new Animation<>(footer);
		this.headerEnabled = true;
		this.footerEnabled = true;
		this.contentChanged = true;

		this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, this::update, 0, 0);
	}

	public void queueUpdate() {
		this.contentChanged = true;
	}

	private void update() {
		boolean updateHeader = this.contentChanged || this.header.shouldUpdate();
		boolean updateFooter = this.contentChanged || this.footer.shouldUpdate();

		if (updateHeader)
			this.headerBuffer = this.headerEnabled ? this.replacePlaceholders(this.header.getCurrentFrame()) : "";

		if (updateFooter)
			this.footerBuffer = this.footerEnabled ? this.replacePlaceholders(this.footer.getCurrentFrame()) : "";

		if(updateHeader || updateFooter) {
			this.contentChanged = false;
			VERISON_ADAPTER.sendTablist(this.player.getPlayer(), this.headerBuffer, this.footerBuffer);
		}

		this.header.tick();
		this.footer.tick();
	}

	protected String replacePlaceholders(String input) {
		return input;
	}

	public void setHeader(AnimationData<String> header) {
		this.header = new Animation<>(header);
		this.contentChanged = true;
	}

	public void setFooter(AnimationData<String> footer) {
		this.footer = new Animation<>(footer);
		this.contentChanged = true;
	}

	public void setHeaderEnabled(boolean headerEnabled) {
		this.contentChanged = headerEnabled != this.headerEnabled;
		this.headerEnabled = headerEnabled;
	}

	public void setFooterEnabled(boolean footerEnabled) {
		this.contentChanged = footerEnabled != this.footerEnabled;
		this.footerEnabled = footerEnabled;
	}

	public void destroy() {
		Bukkit.getScheduler().cancelTask(this.task);
	}
}
