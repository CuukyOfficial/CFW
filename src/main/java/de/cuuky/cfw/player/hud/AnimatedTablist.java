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

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.player.CustomPlayer;

public class AnimatedTablist {

	private final StaticTablist tablist;
	private final int task;
	private Animation<String> header;
	private Animation<String> footer;
	private boolean contentChanged;

	public AnimatedTablist(JavaPlugin javaPlugin, CustomPlayer player, AnimationData<String> header, AnimationData<String> footer) {
		this.header = new Animation<>(header);
		this.footer = new Animation<>(footer);

		this.tablist = new StaticTablist(player) {
			@Override
			protected String processString(String input) {
				return AnimatedTablist.this.processString(input);
			}
		};
		this.update();
		this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, this::update, 0, 0);
	}

	public void queueUpdate() {
		this.contentChanged = true;
	}

	private void update() {
		String headerContent = null;
		String footerContent = null;

		if ((this.contentChanged || this.header.shouldUpdate()) && this.tablist.isHeaderEnabled())
			headerContent = this.header.getCurrentFrame();

		if ((this.contentChanged || this.footer.shouldUpdate()) && this.tablist.isFooterEnabled())
			footerContent = this.footer.getCurrentFrame();

		if (this.contentChanged || headerContent != null || footerContent != null) {
			this.tablist.update(headerContent, footerContent);
			this.contentChanged = false;
		}

		this.header.tick();
		this.footer.tick();
	}

	protected String processString(String input) {
		return input;
	}

	public void setHeader(AnimationData<String> header) {
		this.header = new Animation<>(header);
		this.contentChanged = true;
	}

	public void setFooter(AnimationData<String> footer) {
		this.footer = new Animation<>(footer);
		this.contentChanged = true;
	}

	public void setHeaderEnabled(boolean headerEnabled) {
		this.tablist.setHeaderEnabled(headerEnabled);
		this.contentChanged = true;
	}

	public boolean isHeaderEnabled() {
		return this.tablist.isHeaderEnabled();
	}

	public void setFooterEnabled(boolean footerEnabled) {
		this.tablist.setFooterEnabled(footerEnabled);
		this.contentChanged = true;
	}

	public boolean isFooterEnabled() {
		return this.tablist.isFooterEnabled();
	}

	public void destroy() {
		Bukkit.getScheduler().cancelTask(this.task);
	}
}
