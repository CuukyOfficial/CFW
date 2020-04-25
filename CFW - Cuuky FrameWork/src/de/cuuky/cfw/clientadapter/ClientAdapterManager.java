package de.cuuky.cfw.clientadapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.clientadapter.board.BoardUpdateHandler;
import de.cuuky.cfw.clientadapter.board.CustomBoard;
import de.cuuky.cfw.clientadapter.board.CustomBoardType;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;

public class ClientAdapterManager extends FrameworkManager {
	
	private BoardUpdateHandler updateHandler;

	private ArrayList<CustomBoard> boards;
	private HashMap<CustomBoardType, Boolean> boardTypesEnabled;

	public ClientAdapterManager(JavaPlugin ownerInstance) {
		super(FrameworkManagerType.CLIENT_ADAPTER, ownerInstance);
		
		this.boards = new ArrayList<>();
		this.boardTypesEnabled = new HashMap<>();

		for (CustomBoardType type : CustomBoardType.values())
			this.boardTypesEnabled.put(type, true);
	}

	public void setBoardTypeEnabled(CustomBoardType type, boolean enabled) {
		boardTypesEnabled.put(type, enabled);
	}

	public boolean isBoardTypeEnabled(CustomBoardType type) {
		return boardTypesEnabled.get(type);
	}

	public void setUpdateHandler(BoardUpdateHandler updateHandler) {
		this.updateHandler = updateHandler;
	}

	public BoardUpdateHandler getUpdateHandler() {
		return updateHandler;
	}

	public CustomBoard registerBoard(CustomBoard board) {
		board.setManager(this);
		this.boards.add(board);
		return board;
	}

	public boolean unregisterBoard(CustomBoard board) {
		return this.boards.remove(board);
	}

	public ArrayList<CustomBoard> getBoards(CustomBoardType type) {
		ArrayList<CustomBoard> rBoards = new ArrayList<>();
		for (CustomBoard board : boards)
			if (board.getBoardType() == type)
				rBoards.add(board);

		return rBoards;
	}
}