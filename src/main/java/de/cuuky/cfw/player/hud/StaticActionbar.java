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

package de.cuuky.cfw.player.hud;

import org.bukkit.entity.Player;

import de.cuuky.cfw.player.CustomPlayer;
import de.cuuky.cfw.version.VersionAdapter;
import de.cuuky.cfw.version.VersionUtils;

public class StaticActionbar {
	
	private static final VersionAdapter VERSION_ADAPTER = VersionUtils.getVersionAdapter();

	private final Player player;
	private boolean enabled;

	public StaticActionbar(Player player) {
		this.player = player;
		this.enabled = true;
	}
	
	public StaticActionbar(CustomPlayer player) {
		this(player.getPlayer());
	}

	public void update(String content) {
		VERSION_ADAPTER.sendActionbar(this.player.getPlayer(), this.isEnabled() ? this.processString(content) : "");
	}

	protected String processString(String input) {
		return input;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
