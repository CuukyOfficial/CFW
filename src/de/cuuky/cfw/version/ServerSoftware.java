package de.cuuky.cfw.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public enum ServerSoftware {

	BUKKIT("Bukkit", false, null, "Bukkit"),
	SPIGOT("Spigot", false, SpigotVersionAdapter::new, "Spigot"),
	PAPER("PaperSpigot", false, SpigotVersionAdapter::new, "PaperSpigot", "Paper"),
	TACO("TacoSpigot", false, SpigotVersionAdapter::new, "TacoSpigot"),
	MAGMA("Magma", true, versionSupplier -> new MagmaVersionAdapter(), "Magma"),
	CAULDRON("Cauldron", true, null, "Cauldron"),
	THERMOS("Thermos", true, null, "Thermos"),
	URANIUM("Uranium", true, null, "Uranium"),
	UNKNOWN("Unknown", true, null);

	private static final List<Character> abc =
			new ArrayList<>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
					'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));

	private final String name;
	private final String[] versionnames;
	private final boolean modsupport;
	private final Function<Supplier<VersionAdapter>, VersionAdapter> adapterFunction;
	private VersionAdapter adapter;

	ServerSoftware(String name, boolean modsupport,
				   Function<Supplier<VersionAdapter>, VersionAdapter> adapterFunction, String... versionnames) {
		this.name = name;
		this.versionnames = versionnames;
		this.modsupport = modsupport;
		this.adapterFunction = adapterFunction == null ? Supplier::get : adapterFunction;
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
				}
			}
		}

		return found;
	}
}