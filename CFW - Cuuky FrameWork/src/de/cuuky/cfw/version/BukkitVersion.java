package de.cuuky.cfw.version;

public enum BukkitVersion {

	ONE_10(10),
	ONE_11(11),
	ONE_12(12),
	ONE_13(13),
	ONE_14(14),
	ONE_15(15),
	ONE_16(16),
	ONE_7(7),
	ONE_8(8),
	ONE_9(9);

	private int identifier;

	private BukkitVersion(int identifier) {
		this.identifier = identifier;
	}

	public boolean isHigherThan(BukkitVersion ver) {
		return identifier > ver.identifier;
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