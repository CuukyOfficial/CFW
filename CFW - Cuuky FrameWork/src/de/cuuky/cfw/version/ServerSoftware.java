package de.cuuky.cfw.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ServerSoftware {

	BUKKIT("Bukkit", false, false, "Bukkit"),
	SPIGOT("Spigot", false, false, "Spigot"),
	PAPER("PaperSpigot", false, false, "PaperSpigot", "Paper"),
	TACO("TacoSpigot", false, false, "TacoSpigot"),
	MAGMA("Magma", false, true, "Magma"),
	CAULDRON("Cauldron", false, true, "Cauldron"),
	THERMOS("Thermos", false, true, "Thermos"),
	URANIUM("Uranium", false, true, "Uranium"),
	BUKKITFABRIC("BukkitFabric", true, false, "Bukkit4Fabric", "Fabric"),
	UNKNOWN("Unknown", false, false);

	private static List<Character> abc;

	static {
		abc = new ArrayList<Character>(Arrays.asList(new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' }));
	}

	private String name;
	private String[] versionnames;
	private boolean modsupport;
	private boolean forgeSupport;
	private boolean fabricSupport;

	private ServerSoftware(String name, boolean fabricSupport, boolean forgeSupport, String... versionnames) {
		this.name = name;
		this.versionnames = versionnames;
		this.fabricSupport = fabricSupport;
		this.forgeSupport = forgeSupport;
		this.modsupport = (forgeSupport || fabricSupport);
	}

	public String getName() {
		return this.name;
	}

	public String[] getVersionNames() {
		return this.versionnames;
	}

	public boolean hasModSupport() {
		return this.modsupport;
	}

	public boolean hasForgeSupport() {
		return forgeSupport;
	}

	public boolean hasFabricSupport() {
		return fabricSupport;
	}

	public static ServerSoftware getServerSoftware(String version, String name) {
		version = version.toLowerCase();
		name = name.toLowerCase();

		ServerSoftware found = null;
		String foundName = null;
		for (ServerSoftware software : values()) {
			for (String softwareName : software.getVersionNames()) {
				softwareName = softwareName.toLowerCase();

				if (!version.contains(softwareName) || found != null && softwareName.length() < foundName.length())
					continue;

				found = software;
				foundName = softwareName;
			}
		}

		if (found == null)
			found = UNKNOWN;
		else if (found != UNKNOWN) {
			int location = version.indexOf(foundName);

			if (location - 1 > -1)
				if (abc.contains(version.charAt(location - 1)))
					found = UNKNOWN;

			if (location + foundName.length() + 1 < version.length())
				if (abc.contains(version.charAt(location + foundName.length())))
					found = UNKNOWN;
		}

		if (found == UNKNOWN) {
			for (ServerSoftware software : values()) {
				for (String softwareName : software.getVersionNames()) {
					softwareName = softwareName.toLowerCase();

					if (!name.equals(softwareName))
						continue;

					found = software;
					foundName = softwareName;
				}
			}
		}

		return found;
	}
}