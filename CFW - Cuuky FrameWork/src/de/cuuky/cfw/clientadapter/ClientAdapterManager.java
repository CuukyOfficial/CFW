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