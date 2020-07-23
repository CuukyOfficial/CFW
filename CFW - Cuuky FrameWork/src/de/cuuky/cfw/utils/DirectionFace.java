package de.cuuky.cfw.utils;

public enum DirectionFace {

	NORTH("NORTH", 135f, -135f),
	EAST("EAST", -135f, -45f),
	SOUTH("SOUTH", -45f, 45f),
	WEST("WEST", 45f, 135f);

	private String identifier;
	private float start, end;

	private DirectionFace(String identifier, float start, float end) {
		this.identifier = identifier;
		this.start = start;
		this.end = end;
	}

	public String getIdentifier() {
		return identifier;
	}

	public boolean isIn(float yaw) {
		return start <= yaw && end > yaw;
	}

	public double[] modifyValues(double x, double z) {
		switch (this) {
		case EAST:
			return new double[] { -z, x };
		case WEST:
			return new double[] { z, -x };
		case SOUTH:
			return new double[] { -x, -z };
		default:
			break;
		}

		return new double[] { x, z };
	}

	public static DirectionFace getFace(float yaw) {
		yaw = yaw >= 180 ? -180 + (yaw - 180) : yaw;
		for (DirectionFace face : values())
			if (face.isIn(yaw))
				return face;

		return NORTH;
	}
}