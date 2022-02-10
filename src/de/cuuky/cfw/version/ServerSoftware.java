package de.cuuky.cfw.version;

import java.util.function.Function;
import java.util.function.Supplier;

public enum ServerSoftware {

	BUKKIT("CraftBukkit", null, "CraftBukkit", "Bukkit"),
	SPIGOT("Spigot", SpigotVersionAdapter::new, "Spigot"),
	PAPER("Paper", SpigotVersionAdapter::new, "Paper", "PaperSpigot"),
	SPORT_PAPER("SportPaper", SpigotVersionAdapter::new, "SportPaper"),
	TACO("TacoSpigot", SpigotVersionAdapter::new, "Taco", "TacoSpigot"),
	NACHO("NachoSpigot", SpigotVersionAdapter::new, "Nacho", "NachoSpigot"),
	MAGMA("Magma", versionSupplier -> new MagmaVersionAdapter(), "Magma"),
	THERMOS("Thermos", null, "Thermos"),
	CRUCIBLE("Crucible", null, "Crucible"),

	@Deprecated // Unused
	URANIUM("Uranium", null, "Uranium"),
	@Deprecated // Unused
	CAULDRON("Cauldron", null, "Cauldron"),

	UNKNOWN("Unknown", null);

	private static final String forgeClass = "net.minecraftforge.common.MinecraftForge";

	private final String name;
	private final String[] versionnames;
	private final boolean forgeSupport;
	private final Function<Supplier<VersionAdapter>, VersionAdapter> adapterFunction;
	private VersionAdapter adapter;

	private static ServerSoftware currentSoftware = null;


	/**
	 * @param name Display name for platform.
	 * @param adapterFunction Version adapter for this platform
	 * @param versionnames Names the platform could be known as
	 */
	ServerSoftware(String name, Function<Supplier<VersionAdapter>, VersionAdapter> adapterFunction, String... versionnames) {
		this.name = name;
		this.versionnames = versionnames;
		this.forgeSupport = isClassPresent(forgeClass);
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
	 * @deprecated use {@link #hasForgeSupport()} instead
	 **/
	@Deprecated
	public boolean hasModSupport() {
		return this.hasForgeSupport();
	}

	/**
	 * @return Whether the software has support for Forge mods
	 **/
	public boolean hasForgeSupport() {
		return this.forgeSupport;
	}

	/**
	 * @param clazz Class you want to check
	 * @return Whether the provided class is loaded
	 **/
	private static boolean isClassPresent(String clazz) {
		try {
			Class.forName(clazz);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	VersionAdapter getVersionAdapter(Supplier<VersionAdapter> bukkitVersionSupplier) {
		if (this.adapter == null)
			return this.adapter = this.adapterFunction.apply(bukkitVersionSupplier);
		else
			return this.adapter;
	}

	/**
	 * @return Software the server is running on
	 * @deprecated use {@link #getServerSoftware()} instead
	 **/
	static ServerSoftware getServerSoftware(String version, String name) {
		return getServerSoftware();
	}

	/**
	 * @return Software the server is running on or the next highest one on the fork chain
	 **/
	static ServerSoftware getServerSoftware() {

		// Don't check every time, it's not going to change
		if (currentSoftware == null) {

			// Order is important due to fork chain
			if (isClassPresent("org.magmafoundation.magma.Magma"))
				currentSoftware = MAGMA;
			else if (isClassPresent("io.github.crucible.Crucible"))
				currentSoftware = CRUCIBLE;
			else if (isClassPresent("thermos.Thermos"))
				currentSoftware = THERMOS;
			else if (isClassPresent("org.github.paperspigot.SharedConfig"))
				currentSoftware = SPORT_PAPER;
			else if (isClassPresent("me.elier.nachospigot.config.NachoConfig"))
				currentSoftware = NACHO;
			else if (isClassPresent("net.techcable.tacospigot.TacoSpigotConfig"))
				currentSoftware = TACO;
			else if (isClassPresent("co.aikar.timings.Timings"))
				currentSoftware = PAPER;
			else if (isClassPresent("org.spigotmc.SpigotConfig"))
				currentSoftware = SPIGOT;
			else if (isClassPresent("org.bukkit.craftbukkit.CraftServer") || isClassPresent("org.bukkit.craftbukkit.Main"))
				currentSoftware = BUKKIT;
		}

		return currentSoftware;
	}
}