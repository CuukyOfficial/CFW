package de.cuuky.cfw.clientadapter.board;

import de.cuuky.cfw.clientadapter.ClientAdapterManager;
import de.cuuky.cfw.player.CustomPlayer;

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