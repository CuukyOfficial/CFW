package de.cuuky.cfw.configuration.placeholder.placeholder;

import de.cuuky.cfw.configuration.placeholder.MessagePlaceholder;
import de.cuuky.cfw.configuration.placeholder.placeholder.type.MessagePlaceholderType;

public abstract class GeneralMessagePlaceholder extends MessagePlaceholder {

	private String value;
	private long lastRefresh = 0;

	public GeneralMessagePlaceholder(String identifier, int refreshDelay, boolean rawIdentifier, String description) {
		super(MessagePlaceholderType.GENERAL, identifier, refreshDelay, rawIdentifier, description);
	}

	public GeneralMessagePlaceholder(String identifier, int refreshDelay, String description) {
		this(identifier, refreshDelay, false, description);
	}

	private void checkRefresh() {
		if (!this.shallRefresh(this.lastRefresh)) return;
		this.refreshValue();
	}

	private void refreshValue() {
		this.value = getValue();
		this.lastRefresh = System.currentTimeMillis();
	}

	protected abstract String getValue();

	@Override
	public String replacePlaceholder(String message, Object... objects) {
		this.checkRefresh();
		return message.replace(this.getIdentifier(), this.value != null ? this.value : "");
	}

	@Override
	public void clearValue() {
		this.value = null;
		this.lastRefresh = 0;
	}
}