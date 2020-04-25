package de.cuuky.cfw.configuration.placeholder;

import de.cuuky.cfw.configuration.placeholder.placeholder.type.PlaceholderType;

public abstract class MessagePlaceholder {

	private static final int TICK_TOLERANCE = 900;

	protected PlaceholderType type;
	protected String identifier, description;
	protected int defaultRefresh, refreshDelay;
	protected MessagePlaceholderManager managar;

	public MessagePlaceholder(PlaceholderType type, String identifier, int refreshDelay, String description) {
		this(type, identifier, refreshDelay, false, description);
	}

	public MessagePlaceholder(PlaceholderType type, String identifier, int refreshDelay, boolean rawIdentifier, String description) {
		this.type = type;
		
		if (rawIdentifier)
			this.identifier = identifier;
		else
			this.identifier = "%" + identifier + "%";

		this.description = description;
		this.defaultRefresh = refreshDelay;
		this.refreshDelay = (int) (refreshDelay * 1000);
	}

	protected boolean shallRefresh(long last) {
		if (last < 1)
			return true;

		return (last + this.refreshDelay) - TICK_TOLERANCE <= System.currentTimeMillis() ? true : false;
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
	
	public void setManager(MessagePlaceholderManager managaer) {
		this.managar = managaer;
	}
	
	public MessagePlaceholderManager getManager() {
		return this.managar;
	}

	public abstract void clearValue();
}