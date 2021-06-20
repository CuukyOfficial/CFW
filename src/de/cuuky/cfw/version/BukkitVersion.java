package de.cuuky.cfw.version;

import java.util.function.Supplier;

public enum BukkitVersion {

	ONE_17(17, OneSeventeenVersionAdapter::new),
	ONE_16(16, OneThirteenVersionAdapter::new),
	ONE_15(15, OneThirteenVersionAdapter::new),
	ONE_14(14, OneThirteenVersionAdapter::new),
	ONE_13(13, OneThirteenVersionAdapter::new),
	ONE_12(12, OneTwelveVersionAdapter::new),
	ONE_11(11, OneNineVersionAdapter::new),
	ONE_10(10, OneNineVersionAdapter::new),
	ONE_9(9, OneNineVersionAdapter::new),
	ONE_8(8, OneEightVersionAdapter::new),
	ONE_7(7, OneSevenVersionAdapter::new);

	private final int identifier;
	private final Supplier<VersionAdapter> adapterSupplier;
	private VersionAdapter adapter;

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
	
	public VersionAdapter getAdapter() {
		if(this.adapter == null)
			return this.adapter = this.adapterSupplier.get();
		else
			return this.adapter;
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