package de.cuuky.cfw.version;

import java.util.function.Supplier;

public enum BukkitVersion {

	ONE_17(17, OneSeventeenVersionAdapter::new),
	ONE_16(16, OneSixteenVersionAdapter::new),
	ONE_15(15, OneFourteenVersionAdapter::new),
	ONE_14(14, OneFourteenVersionAdapter::new),
	ONE_13(13, OneThirteenVersionAdapter::new),
	ONE_12(12, OneTwelveVersionAdapter::new),
	ONE_11(11, OneNineVersionAdapter::new),
	ONE_10(10, OneNineVersionAdapter::new),
	ONE_9(9, OneNineVersionAdapter::new),
	ONE_8(8, OneEightVersionAdapter::new),
	ONE_7(7, OneSevenVersionAdapter::new),
	UNSUPPORTED(0, UnsupportedVersionAdapter::new);

	private final int identifier;
	private final Supplier<VersionAdapter> adapterSupplier;

	BukkitVersion(int identifier, Supplier<VersionAdapter> adapterSupplier) {
		this.identifier = identifier;
		this.adapterSupplier = adapterSupplier;
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

	Supplier<VersionAdapter> getAdapterSupplier() {
		return this.adapterSupplier;
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