package de.cuuky.cfw.version;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

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
		Map<Integer, BukkitVersion> sorted = new TreeMap<Integer, BukkitVersion>();
		int versionNumber = Integer.valueOf(versionString.split("1_")[1].split("_")[0]);
		for (BukkitVersion version : values()) {
			if (versionNumber == version.identifier)
				return version;

			sorted.put(version.getIdentifier(), version);
		}

		ArrayList<BukkitVersion> inOrder = new ArrayList<BukkitVersion>();
		for (int i : sorted.keySet())
			inOrder.add(sorted.get(i));

		if (versionNumber < inOrder.get(1).getIdentifier())
			return inOrder.get(0);
		else if (versionNumber > inOrder.get(inOrder.size() - 2).getIdentifier())
			return inOrder.get(inOrder.size() - 1);

		return null;
	}
}