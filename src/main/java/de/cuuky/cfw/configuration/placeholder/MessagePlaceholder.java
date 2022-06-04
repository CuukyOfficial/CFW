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
        if (last < 1)
            return true;
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
