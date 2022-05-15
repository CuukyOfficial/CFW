package de.cuuky.cfw.player.hud;

public class ScoreboardAnimationData extends AnimationData<String[]> {

	public ScoreboardAnimationData(int delay, String[][] frames) {
		super(delay, frames);
	}

	@Override
	protected void processFrames(String[][] frames) {
		for (String[] frame : frames) {
			String space = "";
			for (int i = 0; i < frame.length; i++) {
				if (frame[i].equals("%space%")) {
					space += " ";
					frame[i] = space;
				}
			}
		}
	}
}
