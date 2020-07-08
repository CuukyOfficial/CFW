package de.cuuky.cfw.manager;

import org.bukkit.plugin.java.JavaPlugin;

public class FrameworkManager {

	protected JavaPlugin ownerInstance;
	protected String consolePrefix;
	protected FrameworkManagerType type;

	public FrameworkManager(FrameworkManagerType type, JavaPlugin ownerInstance) {
		this.type = type;
		this.consolePrefix = "[" + ownerInstance.getName() + "] ";
		this.ownerInstance = ownerInstance;
	}

	public JavaPlugin getOwnerInstance() {
		return this.ownerInstance;
	}

	public FrameworkManagerType getType() {
		return this.type;
	}

	public String getConsolePrefix() {
		return consolePrefix;
	}
}