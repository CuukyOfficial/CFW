package de.cuuky.cfw.configuration.placeholder;

import de.cuuky.cfw.configuration.placeholder.placeholder.type.PlaceholderType;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import javafx.util.Pair;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessagePlaceholderManager extends FrameworkManager {

	private int tickTolerance = 900;

	private final Map<PlaceholderType, List<MessagePlaceholder>> placeholders = new HashMap<>();
	private final Map<PlaceholderType, Map<String, List<MessagePlaceholder>>> cachedRequests = new HashMap<>();

	public MessagePlaceholderManager(JavaPlugin instance) {
		super(FrameworkManagerType.PLACEHOLDER, instance);
	}

	private Pair<String, List<MessagePlaceholder>> replaceByList(String value, List<MessagePlaceholder> list, boolean check, Object... args) {
		if (list == null) return new Pair<>(value, null);

		List<MessagePlaceholder> cached = new ArrayList<>();
		for (MessagePlaceholder pmp : list) {
			if (check && !pmp.containsPlaceholder(value)) continue;
			value = pmp.replacePlaceholder(value, args);
			if (check) cached.add(pmp);
		}

		return new Pair<>(value, cached);
	}

	public String replacePlaceholders(String value, PlaceholderType type, Object... objects) {
		Map<String, List<MessagePlaceholder>> reqs = cachedRequests.get(type);
		if (reqs == null) reqs = new HashMap<>();

		if (reqs.get(value) != null)
			return replaceByList(value, reqs.get(value), false, objects).getKey();
		else {
			Pair<String, List<MessagePlaceholder>> result = replaceByList(value, getPlaceholders(type), true, objects);
			if (result.getValue() != null) reqs.put(value, result.getValue());
			cachedRequests.put(type, reqs);
			return result.getKey();
		}
	}

	public MessagePlaceholder registerPlaceholder(MessagePlaceholder placeholder) {
		if (placeholders.containsKey(placeholder.getType()))
			placeholders.get(placeholder.getType()).add(placeholder);
		else {
			List<MessagePlaceholder> holders = new ArrayList<>();
			holders.add(placeholder);
			this.placeholders.put(placeholder.getType(), holders);
		}

		return placeholder;
	}

	public MessagePlaceholder unregisterPlaceholder(MessagePlaceholder placeholder) {
		if (placeholders.containsKey(placeholder.getType()))
			placeholders.get(placeholder.getType()).remove(placeholder);

		return placeholder;
	}

	public void clear() {
		for (PlaceholderType type : placeholders.keySet())
			this.placeholders.get(type).forEach(MessagePlaceholder::clearValue);
		this.cachedRequests.clear();
	}

	public int getTickTolerance() {
		return this.tickTolerance;
	}

	public void setTickTolerance(int tickTolerance) {
		this.tickTolerance = tickTolerance;
	}

	public Map<PlaceholderType, List<MessagePlaceholder>> getPlaceholders() {
		return this.placeholders;
	}

	public List<MessagePlaceholder> getAllPlaceholders() {
		return this.placeholders.keySet().stream().flatMap(type -> this.placeholders.get(type).stream()).collect(Collectors.toList());
	}

	public List<MessagePlaceholder> getPlaceholders(PlaceholderType type) {
		return placeholders.get(type);
	}
}