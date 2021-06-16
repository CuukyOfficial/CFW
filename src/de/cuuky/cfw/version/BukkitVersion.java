package de.cuuky.cfw.version;

public enum BukkitVersion {

	ONE_17(17),
	ONE_16(16),
	ONE_15(15),
	ONE_14(14),
	ONE_13(13),
	ONE_12(12),
	ONE_11(11),
	ONE_10(10),
	ONE_9(9),
	ONE_8(8),
	ONE_7(7);

	private int identifier;

	BukkitVersion(int identifier) {
		this.identifier = identifier;
	}

	public boolean isHigherThan(BukkitVersion ver) {
		return this.identifier > ver.getIdentifier();
	}

	public boolean isLowerThan(BukkitVersion ver) {
		return this.identifier < ver.getIdentifier();
	}

	public int getIdentifier() {
		return this.identifier;
	}

	public static BukkitVersion getVersion(String versionString) {
		int versionNumber = Integer.valueOf(versionString.split("1_")[1].split("_")[0]);
		BukkitVersion nextFound = BukkitVersion.ONE_7;
		for (BukkitVersion version : values()) {
			if (versionNumber == version.getIdentifier())
				return version;

			if (versionNumber > version.getIdentifier()) {
				if (nextFound.getIdentifier() > version.getIdentifier())
					continue;

				nextFound = version;
			}
		}

		return nextFound;
	}
}