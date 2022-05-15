package de.cuuky.cfw.player.hud;

class Animation<T> {

	private AnimationData<T> data;
	private long ticksPassed;

	Animation(AnimationData<T> data) {
		this.data = data;
	}

	void tick() {
		this.ticksPassed++;
	}

	void reset() {
		this.ticksPassed = 0;
	}

	boolean shouldUpdate() {
		return this.data.delay == 0 ? false : this.ticksPassed % this.data.delay == 0;
	}

	T getCurrentFrame() {
		return this.data.delay == 0 ? this.data.frames[0]
				: this.data.frames[((int) (this.ticksPassed / this.data.delay)) % this.data.frames.length];
	}
}
