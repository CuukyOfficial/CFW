/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cuuky.cfw.configuration.placeholder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.configuration.placeholder.placeholder.type.PlaceholderType;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;

public class MessagePlaceholderManager extends FrameworkManager {

	private int tickTolerance = 900;

	private final Map<PlaceholderType, List<MessagePlaceholder>> placeholders = new HashMap<>();
	private final Map<PlaceholderType, Map<String, List<MessagePlaceholder>>> cachedRequests = new HashMap<>();

	public MessagePlaceholderManager(JavaPlugin instance) {
		super(FrameworkManagerType.PLACEHOLDER, instance);
	}

	private List<MessagePlaceholder> search(PlaceholderType type, String containing) {
		return this.getPlaceholders(type).stream().filter(mp -> mp.containsPlaceholder(containing)).collect(Collectors.toList());
	}

	private String replaceByList(String value, List<MessagePlaceholder> list, Object... args) {
		if (list == null)
			return null;
		for (MessagePlaceholder pmp : list)
			value = pmp.replacePlaceholder(value, args);
		return value;
	}

	public String replacePlaceholders(String value, PlaceholderType type, Object... objects) {
		Map<String, List<MessagePlaceholder>> reqs = cachedRequests.get(type);
		List<MessagePlaceholder> placeholders;
		if (reqs == null)
			reqs = new HashMap<>();
		if ((placeholders = reqs.get(value)) == null) {
			reqs.put(value, placeholders = this.search(type, value));
			cachedRequests.put(type, reqs);
		}

		return replaceByList(value, placeholders, objects);
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
