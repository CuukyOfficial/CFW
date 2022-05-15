package de.cuuky.cfw.player.hud;

import java.util.Arrays;
import java.util.List;

public class TablistAnimationData extends AnimationData<String> {

	private static final String NEW_LINE = "\n";

	public TablistAnimationData(int delay, String[] frames) {
		super(delay, frames);
	}

	public TablistAnimationData(int delay, String[][] frames) {
		this(delay, Arrays.stream(frames).map(frame -> String.join(NEW_LINE, frame)).toArray(String[]::new));
	}

	public TablistAnimationData(int delay, List<List<String>> frames) {
		this(delay, frames.stream().map(frame -> String.join(NEW_LINE, frame)).toArray(String[]::new));
	}
}
