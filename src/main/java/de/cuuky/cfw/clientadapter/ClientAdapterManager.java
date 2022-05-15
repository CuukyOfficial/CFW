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

package de.cuuky.cfw.clientadapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import de.cuuky.cfw.clientadapter.board.CustomBoard;
import de.cuuky.cfw.clientadapter.board.CustomBoardType;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import de.cuuky.cfw.player.CustomPlayer;

@Deprecated
public class ClientAdapterManager<T extends CustomPlayer> extends FrameworkManager {

	private List<CustomBoard<T>> boards;
	private Map<CustomBoardType, Boolean> boardTypesEnabled;

	public ClientAdapterManager(JavaPlugin instance) {
		super(FrameworkManagerType.CLIENT_ADAPTER, instance);

		this.boards = new ArrayList<>();
		this.boardTypesEnabled = new HashMap<>();

		for (CustomBoardType type : CustomBoardType.values())
			this.boardTypesEnabled.put(type, false);
	}

	public BukkitTask addUpdateTask(long delay, long period, CustomBoardType... board) {
		return new BukkitRunnable() {

			@Override
			public void run() {
				updateBoards(board);
			}
		}.runTaskTimerAsynchronously(this.ownerInstance, delay, period);
	}

	public void updateBoards(CustomBoardType... bType) {
		for (CustomBoardType type : bType)
			this.boards.stream().filter(board -> board.getBoardType() == type).forEach(board -> board.update());
	}

	public void updateBoards() {
		this.boardTypesEnabled.keySet().forEach(type -> updateBoards(type));
	}

	public void setBoardTypeEnabled(boolean enabled, CustomBoardType... board) {
		for (CustomBoardType type : board)
			boardTypesEnabled.put(type, enabled);
	}

	@Deprecated
	public void setBoardTypeEnabled(CustomBoardType type, boolean enabled) {
		boardTypesEnabled.put(type, enabled);
	}

	public boolean isBoardTypeEnabled(CustomBoardType type) {
		return boardTypesEnabled.get(type);
	}

	public <B extends CustomBoard<T>> B registerBoard(B board) {
		board.setManager(this);
		this.boards.add(board);
		return board;
	}

	public <B extends CustomBoard<T>> boolean unregisterBoard(B board) {
		return this.boards.remove(board);
	}

	public ArrayList<CustomBoard<T>> getBoards(CustomBoardType type) {
		ArrayList<CustomBoard<T>> rBoards = new ArrayList<>();
		for (CustomBoard<T> board : boards)
			if (board.getBoardType() == type)
				rBoards.add(board);

		return rBoards;
	}

	public <B extends CustomBoard<T>> ArrayList<B> getBoards(Class<B> boardClass) {
		ArrayList<B> rBoards = new ArrayList<>();
		for (CustomBoard<T> board : boards)
			if (boardClass.isAssignableFrom(board.getClass()))
				rBoards.add((B) board);

		return rBoards;
	}
}