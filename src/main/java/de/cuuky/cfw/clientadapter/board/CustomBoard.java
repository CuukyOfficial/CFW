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

package de.cuuky.cfw.clientadapter.board;

import de.cuuky.cfw.clientadapter.ClientAdapterManager;
import de.cuuky.cfw.player.CustomPlayer;

@Deprecated
public abstract class CustomBoard<T extends CustomPlayer> {

	protected ClientAdapterManager<T> manager;
	protected T player;

	private CustomBoardType boardType;
	private boolean initalized, enabled;

	public CustomBoard(CustomBoardType boardType, T player) {
		this.player = player;
		this.boardType = boardType;
		this.enabled = true;
	}

	protected abstract void onEnable();

	protected abstract void onUpdate();

	public void remove() {
		this.manager.unregisterBoard(this);
	}

	public void setManager(ClientAdapterManager<T> manager) {
		this.manager = manager;

		if (this.manager.isBoardTypeEnabled(this.boardType) && enabled) {
			onEnable();
			initalized = true;

			update();
		}
	}

	public void update() {
		if (this.player.getPlayer() == null || !enabled)
			return;

		if (!this.manager.isBoardTypeEnabled(this.boardType))
			return;
		else if (!initalized) {
			onEnable();
			this.initalized = true;
		}

		onUpdate();
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public CustomPlayer getPlayer() {
		return player;
	}

	public CustomBoardType getBoardType() {
		return boardType;
	}
}