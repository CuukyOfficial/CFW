package de.cuuky.cfw.configuration.placeholder;

import de.cuuky.cfw.configuration.placeholder.placeholder.type.PlaceholderType;

public abstract class MessagePlaceholder {

	private final PlaceholderType type;
	private final String identifier, description;
	private final int defaultRefresh, refreshDelay;
	private int tickTolerance = 900;
	private MessagePlaceholderManager manager;

	public MessagePlaceholder(PlaceholderType type, String identifier, int refreshDelay, boolean rawIdentifier, String description) {
		this.type = type;
		this.identifier = rawIdentifier ? identifier : "%" + identifier + "%";
		this.description = description;
		this.defaultRefresh = refreshDelay;
		this.refreshDelay = refreshDelay * 1000;
	}

	public MessagePlaceholder(PlaceholderType type, String identifier, int refreshDelay, String description) {
		this(type, identifier, refreshDelay, false, description);
	}

	public abstract String replacePlaceholder(String message, Object... object);

	protected boolean shallRefresh(long last) {
		if (last < 1) return true;
		return (last + this.refreshDelay) - this.getTickTolerance() <= System.currentTimeMillis();
	}

	public boolean containsPlaceholder(String message) {
		return message.contains(identifier);
	}

	public PlaceholderType getType() {
		return this.type;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public String getDescription() {
		return this.description;
	}

	public int getDefaultRefresh() {
		return this.defaultRefresh;
	}

	public void setManager(MessagePlaceholderManager manager) {
		this.manager = manager;
	}

	public int getTickTolerance() {
		return this.manager != null ? this.manager.getTickTolerance() : this.tickTolerance;
	}

	public MessagePlaceholderManager getManager() {
		return this.manager;
	}

	public abstract void clearValue();
}