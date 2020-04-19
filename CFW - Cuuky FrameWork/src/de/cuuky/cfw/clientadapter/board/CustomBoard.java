package de.cuuky.cfw.clientadapter.board;

import de.cuuky.cfw.clientadapter.ClientAdapterManager;
import de.cuuky.cfw.player.CustomPlayer;

public abstract class CustomBoard {

	protected ClientAdapterManager manager;
	protected CustomPlayer player;
	protected boolean enabled;

	private CustomBoardType boardType;

	public CustomBoard(CustomBoardType boardType, CustomPlayer player) {
		this.player = player;
		this.boardType = boardType;
		this.enabled = true;
	}

	protected abstract void onEnable();

	protected abstract void onUpdate();

	public void remove() {
		this.manager.unregisterBoard(this);
	}

	public void setManager(ClientAdapterManager manager) {
		this.manager = manager;

		if (this.manager.isBoardTypeEnabled(this.boardType)) {
			onEnable();
			update();
		}
	}

	public void update() {
		if (!this.manager.isBoardTypeEnabled(this.boardType) || this.player.getPlayer() == null || !enabled)
			return;

		onUpdate();
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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