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

package de.cuuky.cfw.configuration.placeholder.placeholder;

import java.util.HashMap;
import java.util.Map;

import de.cuuky.cfw.configuration.placeholder.MessagePlaceholder;
import de.cuuky.cfw.configuration.placeholder.placeholder.type.MessagePlaceholderType;

public abstract class ObjectMessagePlaceholder<T> extends MessagePlaceholder {

	private final Map<T, String> placeholderValues = new HashMap<>();
	private final Map<T, Long> placeholderRefreshes = new HashMap<>();

	public ObjectMessagePlaceholder(String identifier, int refreshDelay, String description) {
		super(MessagePlaceholderType.OBJECT, identifier, refreshDelay, description);
	}

	private void checkRefresh(T player) {
		if (!this.shallRefresh(player))
			return;
		this.refreshValue(player);
	}

	private boolean shallRefresh(T player) {
		if (!this.placeholderRefreshes.containsKey(player))
			return true;
		return this.shallRefresh(this.placeholderRefreshes.get(player));
	}

	private void refreshValue(T player) {
		this.placeholderValues.put(player, this.getValue(player));
		this.placeholderRefreshes.put(player, System.currentTimeMillis());
	}

	protected abstract String getValue(T object);

	@Override
	public String replacePlaceholder(String message, Object... objects) {
		T object = (T) objects[0];
		this.checkRefresh(object);
		String value = placeholderValues.get(object);
		return message.replace(this.getIdentifier(), value != null ? value : "");
	}

	@Override
	public void clearValue() {
		this.placeholderValues.clear();
		this.placeholderRefreshes.clear();
	}
}
