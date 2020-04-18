package de.cuuky.cfw.clientadapter.board;

import de.cuuky.cfw.clientadapter.ClientAdapterManager;
import de.cuuky.cfw.player.CustomPlayer;

public abstract class CustomBoard {

	protected ClientAdapterManager manager;
	protected CustomPlayer player;

	private CustomBoardType boardType;

	public CustomBoard(CustomBoardType boardType, CustomPlayer player) {
		this.player = player;
		this.boardType = boardType;
	}

	protected abstract void onEnable();

	protected abstract void onUpdate();
	
	public void remove() {
		this.manager.unregisterBoard(this);
	}
	
	public void setManager(ClientAdapterManager manager) {
		this.manager = manager;
		
		if(this.manager.isBoardTypeEnabled(this.boardType)) {
			onEnable();
			update();
		}
	}

	public void update() {
		if(!this.manager.isBoardTypeEnabled(this.boardType) || this.player.getPlayer() == null)
			return;

		onUpdate();
	}

	public BoardUpdateHandler getUpdateHandler() {
		return this.manager.getUpdateHandler();
	}

	public CustomPlayer getPlayer() {
		return player;
	}
	
	public CustomBoardType getBoardType() {
		return boardType;
	}
}