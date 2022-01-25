package de.cuuky.cfw.player;

public class AnimationData<T> {

	int delay;
	T[] frames;

	public AnimationData(int delay, T[] frames) {
		if(delay < 0)
			throw new IllegalArgumentException("Delay < 0");
		
		if(frames == null || frames.length == 0)
			throw new IllegalArgumentException("Frames are null or empty");
		
		this.delay = delay;
		this.frames = frames;
		this.processFrames(frames);
	}
	
	protected void processFrames(T[] frames) {
	}
}
