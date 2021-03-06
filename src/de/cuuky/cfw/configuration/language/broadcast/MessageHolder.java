package de.cuuky.cfw.configuration.language.broadcast;

import java.util.HashMap;
import java.util.Map;

import de.cuuky.cfw.configuration.placeholder.MessagePlaceholderManager;
import de.cuuky.cfw.configuration.placeholder.placeholder.type.MessagePlaceholderType;
import de.cuuky.cfw.player.CustomPlayer;

public class MessageHolder {

	private MessagePlaceholderManager placeholderManager;
	private Map<String, String> replacements;

	public MessageHolder(MessagePlaceholderManager manager) {
		this.placeholderManager = manager;
		this.replacements = new HashMap<String, String>();
	}

	public MessageHolder replace(String needle, String replacement) {
		replacements.put(needle, replacement);
		return this;
	}

	public String getReplaced(String message, CustomPlayer cp) {
		for (String repl : replacements.keySet())
			message = message.replace(repl, replacements.get(repl));

		message = placeholderManager.replacePlaceholders(message, MessagePlaceholderType.GENERAL);
		if (cp != null)
			message = placeholderManager.replacePlaceholders(message, cp, MessagePlaceholderType.PLAYER);
		return message;
	}
}