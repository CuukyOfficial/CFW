package de.cuuky.cfw.clientadapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.clientadapter.board.BoardUpdateHandler;
import de.cuuky.cfw.clientadapter.board.CustomBoard;
import de.cuuky.cfw.clientadapter.board.CustomBoardType;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import de.cuuky.cfw.player.CustomPlayer;

public class ClientAdapterManager<T extends CustomPlayer> extends FrameworkManager {
	
	private BoardUpdateHandler<T> updateHandler;

	private ArrayList<CustomBoard<T>> boards;
	private HashMap<CustomBoardType, Boolean> boardTypesEnabled;

	public ClientAdapterManager(JavaPlugin instance) {
		super(FrameworkManagerType.CLIENT_ADAPTER, instance);
		
		this.boards = new ArrayList<>();
		this.boardTypesEnabled = new HashMap<>();

		for (CustomBoardType type : CustomBoardType.values())
			this.boardTypesEnabled.put(type, false);
	}

	public void setBoardTypeEnabled(CustomBoardType type, boolean enabled) {
		boardTypesEnabled.put(type, enabled);
	}

	public boolean isBoardTypeEnabled(CustomBoardType type) {
		return boardTypesEnabled.get(type);
	}

	public void setUpdateHandler(BoardUpdateHandler<T> updateHandler) {
		this.updateHandler = updateHandler;
	}

	public BoardUpdateHandler<T> getUpdateHandler() {
		return updateHandler;
	}

	public CustomBoard<T> registerBoard(CustomBoard<T> board) {
		board.setManager(this);
		this.boards.add(board);
		return board;
	}

	public boolean unregisterBoard(CustomBoard<T> board) {
		return this.boards.remove(board);
	}

	public ArrayList<CustomBoard<T>> getBoards(CustomBoardType type) {
		ArrayList<CustomBoard<T>> rBoards = new ArrayList<>();
		for (CustomBoard<T> board : boards)
			if (board.getBoardType() == type)
				rBoards.add(board);

		return rBoards;
	}
}