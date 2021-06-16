package de.cuuky.cfw.configuration.placeholder.placeholder;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.cuuky.cfw.configuration.placeholder.MessagePlaceholder;
import de.cuuky.cfw.configuration.placeholder.placeholder.type.MessagePlaceholderType;

public abstract class GeneralMessagePlaceholder extends MessagePlaceholder {

	private static long lastDateRefreshTime;
	private static String[] lastDateRefresh;

	private String value;
	protected long lastRefresh;

	public GeneralMessagePlaceholder(String identifier, int refreshDelay, String description) {
		this(identifier, refreshDelay, false, description);
	}

	public GeneralMessagePlaceholder(String identifier, int refreshDelay, boolean rawIdentifier, String description) {
		super(MessagePlaceholderType.GENERAL, identifier, refreshDelay, rawIdentifier, description);

		this.lastRefresh = 0;
	}

	private void checkRefresh() {
		if (!shallRefresh(this.lastRefresh))
			return;

		refreshValue();
	}

	private void refreshValue() {
		this.value = getValue();
		this.lastRefresh = System.currentTimeMillis();
	}

	protected abstract String getValue();

	public String replacePlaceholder(String message) {
		checkRefresh();

		return message.replace(this.identifier, this.value != null ? this.value : "");
	}

	@Override
	public void clearValue() {
		this.value = null;
		this.lastRefresh = 0;
	}

	protected static String getLastDateRefresh(int index) {
		if (lastDateRefresh == null || lastDateRefreshTime + 900 <= System.currentTimeMillis()) {
			lastDateRefreshTime = System.currentTimeMillis();
			lastDateRefresh = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss").format(new Date()).split(",");
		}

		return lastDateRefresh[index];
	}
}