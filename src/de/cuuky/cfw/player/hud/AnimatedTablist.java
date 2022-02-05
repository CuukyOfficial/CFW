package de.cuuky.cfw.player.hud;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.player.CustomPlayer;

public class AnimatedTablist {

	private final StaticTablist tablist;
	private final int task;
	private Animation<String> header;
	private Animation<String> footer;
	private boolean contentChanged;

	public AnimatedTablist(JavaPlugin javaPlugin, CustomPlayer player, AnimationData<String> header,
			AnimationData<String> footer) {
		this.header = new Animation<>(header);
		this.footer = new Animation<>(footer);

		this.tablist = new StaticTablist(player) {
			@Override
			protected String processString(String input) {
				return AnimatedTablist.this.processString(input);
			}
		};
		this.update();
		this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, this::update, 0, 0);
	}

	public void queueUpdate() {
		this.contentChanged = true;
	}

	private void update() {
		String headerContent = null;
		String footerContent = null;

		if ((this.contentChanged || this.header.shouldUpdate()) && this.tablist.isHeaderEnabled())
			headerContent = this.header.getCurrentFrame();

		if ((this.contentChanged || this.footer.shouldUpdate()) && this.tablist.isFooterEnabled())
			footerContent = this.footer.getCurrentFrame();

		if (this.contentChanged || headerContent != null || footerContent != null) {
			this.tablist.update(headerContent, footerContent);
			this.contentChanged = false;
		}

		this.header.tick();
		this.footer.tick();
	}

	protected String processString(String input) {
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
		this.tablist.setHeaderEnabled(headerEnabled);
		this.contentChanged = true;
	}

	public boolean isHeaderEnabled() {
		return this.tablist.isHeaderEnabled();
	}

	public void setFooterEnabled(boolean footerEnabled) {
		this.tablist.setFooterEnabled(footerEnabled);
		this.contentChanged = true;
	}

	public boolean isFooterEnabled() {
		return this.tablist.isFooterEnabled();
	}

	public void destroy() {
		Bukkit.getScheduler().cancelTask(this.task);
	}
}
