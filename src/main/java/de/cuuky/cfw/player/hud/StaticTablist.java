package de.cuuky.cfw.player.hud;

import de.cuuky.cfw.player.CustomPlayer;
import de.cuuky.cfw.version.VersionAdapter;
import de.cuuky.cfw.version.VersionUtils;

public class StaticTablist {

	private static final VersionAdapter VERISON_ADAPTER = VersionUtils.getVersionAdapter();

	private final CustomPlayer player;
	private String headerBuffer;
	private String footerBuffer;
	private boolean headerEnabled;
	private boolean footerEnabled;

	public StaticTablist(CustomPlayer player, String header, String footer) {
		this.player = player;
		this.headerEnabled = true;
		this.footerEnabled = true;

		this.update(header, footer);
	}

	public StaticTablist(CustomPlayer player) {
		this.player = player;
		this.headerEnabled = false;
		this.footerEnabled = false;
		this.headerBuffer = "";
		this.footerBuffer = "";

		this.update(null, null);
	}

	/**
	 * Updates the tablist
	 * 
	 * @param header The header, may be null
	 * @param footer The footer, may be null
	 */
	public void update(String header, String footer) {
		this.headerBuffer = this.headerEnabled ? header == null ? this.headerBuffer : this.processString(header) : "";
		this.footerBuffer = this.footerEnabled ? footer == null ? this.footerBuffer : this.processString(footer) : "";

		VERISON_ADAPTER.sendTablist(this.player.getPlayer(), this.headerBuffer, this.footerBuffer);
	}

	protected String processString(String input) {
		return input;
	}

	/**
	 * Enables or disables the header. {@link StaticTablist#update} has to be
	 * invoked for the change to take affect
	 * 
	 * @param headerEnabled True if enabled
	 */
	public void setHeaderEnabled(boolean headerEnabled) {
		this.headerEnabled = headerEnabled;
	}

	public boolean isHeaderEnabled() {
		return this.headerEnabled;
	}

	/**
	 * Enables or disables the footer. {@link StaticTablist#update} has to be
	 * invoked for the change to take affect
	 * 
	 * @param footerEnabled True if enabled
	 */
	public void setFooterEnabled(boolean footerEnabled) {
		this.footerEnabled = footerEnabled;
	}

	public boolean isFooterEnabled() {
		return this.footerEnabled;
	}
}
