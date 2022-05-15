package de.cuuky.cfw.version;

import java.util.function.Function;
import java.util.function.Supplier;

public enum ServerSoftware {

	MAGMA("Magma", versionSupplier -> new MagmaVersionAdapter(), "org.magmafoundation.magma.Magma", "Magma"),
	CRUCIBLE("Crucible", versionSupplier -> new CrucibleVersionAdapter(), "io.github.crucible.Crucible", "Crucible"),
	@Deprecated // Unused
	URANIUM("Uranium", null, null, "Uranium"),
	THERMOS("Thermos", null, "thermos.Thermos", "Thermos"),
	@Deprecated // Unused
	CAULDRON("Cauldron", null, null, "Cauldron"),
	SPORT_PAPER("SportPaper", SpigotVersionAdapter::new, "org.github.paperspigot.SharedConfig", "SportPaper"),
	NACHO("NachoSpigot", SpigotVersionAdapter::new, "me.elier.nachospigot.config.NachoConfig", "Nacho", "NachoSpigot"),
	TACO("TacoSpigot", SpigotVersionAdapter::new, "net.techcable.tacospigot.TacoSpigotConfig", "Taco", "TacoSpigot"),
	PAPER("Paper", SpigotVersionAdapter::new, "co.aikar.timings.Timings", "Paper", "PaperSpigot"),
	SPIGOT("Spigot", SpigotVersionAdapter::new, "org.spigotmc.SpigotConfig", "Spigot"),
	BUKKIT("CraftBukkit", null, "org.bukkit.Bukkit", "CraftBukkit", "Bukkit"),
	UNKNOWN("Unknown", null, null);

	private final String name;
	private final String[] versionnames;
	private String identifierClass;
	private final Function<Supplier<VersionAdapter>, VersionAdapter> adapterFunction;
	private VersionAdapter adapter;

	private static ServerSoftware currentSoftware = null;


	/**
	 * @param name Display name for platform.
	 * @param adapterFunction Version adapter for this platform
	 * @param identifierClass Class that identifies this platform
	 * @param versionnames Names the platform could be known as
	 */
	ServerSoftware(String name, Function<Supplier<VersionAdapter>, VersionAdapter> adapterFunction, String identifierClass, String... versionnames) {
		this.name = name;
		this.versionnames = versionnames;
		this.identifierClass = identifierClass == null ? "" : identifierClass;
		this.adapterFunction = adapterFunction == null ? Supplier::get : adapterFunction;
	}
	/**
	 * @return Name of the software
	 **/
	public String getName() {
		return this.name;
	}

	/**
	 * @return Names of the software
	 **/
	public String[] getVersionNames() {
		return this.versionnames;
	}

	/**
	 * @return Whether the software has support for Forge mods
	 * @deprecated use {@link VersionUtils#hasForgeSupport()} instead
	 **/
	@Deprecated
	public boolean hasModSupport() {
		return VersionUtils.hasForgeSupport();
	}

	/**
	 * @return Whether the software has support for Forge mods
	 * @deprecated use {@link VersionUtils#hasForgeSupport()} instead
	 **/
	@Deprecated
	public boolean hasForgeSupport() {
		return VersionUtils.hasForgeSupport();
	}

	/**
	 * @return Name of the identifier class
	 **/
	public String getIdentifierClass() {
		return this.identifierClass;
	}

	VersionAdapter getVersionAdapter(Supplier<VersionAdapter> bukkitVersionSupplier) {
		if (this.adapter == null)
			return this.adapter = this.adapterFunction.apply(bukkitVersionSupplier);
		else
			return this.adapter;
	}
	
	/**
	 * @return Software the server is running on or the next highest one on the fork chain
	 **/
	static ServerSoftware getServerSoftware() {

		// Don't check every time, it's not going to change
		if (currentSoftware == null) {
			// Order is important due to fork chain
			for (ServerSoftware software : values()) {
				if (VersionUtils.isClassPresent(software.getIdentifierClass())) {
					currentSoftware = software;
					break;
				}
			}
		}
		return currentSoftware;
	}

}