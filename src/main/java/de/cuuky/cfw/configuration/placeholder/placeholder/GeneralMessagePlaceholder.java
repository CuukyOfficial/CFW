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