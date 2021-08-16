package de.cuuky.cfw.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public enum ServerSoftware {

	BUKKIT("Bukkit", false, null, "Bukkit"),
	SPIGOT("Spigot", false, versionSupplier -> new SpigotVersionAdapter(versionSupplier), "Spigot"),
	PAPER("PaperSpigot", false, versionSupplier -> new SpigotVersionAdapter(versionSupplier), "PaperSpigot", "Paper"),
	TACO("TacoSpigot", false, versionSupplier -> new SpigotVersionAdapter(versionSupplier), "TacoSpigot"),
	MAGMA("Magma", true, versionSupplier -> new MagmaVersionAdapter(), "Magma"),
	CAULDRON("Cauldron", true, null, "Cauldron"),
	THERMOS("Thermos", true, null, "Thermos"),
	URANIUM("Uranium", true, null, "Uranium"),
	UNKNOWN("Unknown", true, null);

	private static List<Character> abc;

	static {
		abc = new ArrayList<Character>(Arrays.asList(new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' }));
	}

	private String name;
	private String[] versionnames;
	private boolean modsupport;
	private final Function<Supplier<VersionAdapter>, VersionAdapter> adapterFunction;
	private VersionAdapter adapter;

	private ServerSoftware(String name, boolean modsupport,
			Function<Supplier<VersionAdapter>, VersionAdapter> adapterFunction, String... versionnames) {
		this.name = name;
		this.versionnames = versionnames;
		this.modsupport = modsupport;
		this.adapterFunction = adapterFunction == null ? version -> version.get() : adapterFunction;
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

	VersionAdapter getVersionAdapter(Supplier<VersionAdapter> bukkitVersionSupplier) {
		if (this.adapter == null)
			return this.adapter = this.adapterFunction.apply(bukkitVersionSupplier);
		else
			return this.adapter;
	}

	static ServerSoftware getServerSoftware(String version, String name) {
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