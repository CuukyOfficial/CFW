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

package de.cuuky.cfw.player.connection;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.cuuky.cfw.version.VersionAdapter;
import de.cuuky.cfw.version.VersionUtils;

@Deprecated
public class NetworkManager {

	private Player player;
	private VersionAdapter internal;

	public NetworkManager(Player player) {
		this.player = player;
		this.internal = VersionUtils.getVersionAdapter();
	}

	public Object getConnection() {
		return this.internal.getConnection(this.player);
	}

	public int getPing() {
		return this.internal.getPing(this.player);
	}

	public Player getPlayer() {
		return this.player;
	}

	public void respawnPlayer() {
		this.internal.respawnPlayer(this.player);
	}

	public void sendActionbar(String message, int duration, Plugin instance) {
		this.internal.sendActionbar(this.player, message, duration, instance);
	}

	public void sendActionbar(String message) {
		this.internal.sendActionbar(this.player, message);
	}

	public void sendLinkedMessage(String message, String link) {
		this.internal.sendLinkedMessage(this.player, message, link);
	}

	public void sendPacket(Object packet) {
		throw new Error("Unsupported");
	}

	public void sendTablist(String header, String footer) {
		this.internal.sendTablist(this.player, header, footer);
	}

	public void sendTitle(String header, String footer) {
		this.internal.sendTitle(this.player, header, footer);
	}

	public void setAttributeSpeed(double value) {
		this.internal.setAttributeSpeed(this.player, value);
	}

	public Object getNetworkManager() {
		return this.internal.getNetworkManager(this.player);
	}

	public String getLocale() {
		return this.internal.getLocale(this.player);
	}
}
