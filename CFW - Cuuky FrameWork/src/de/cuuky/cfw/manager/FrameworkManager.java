package de.cuuky.cfw.manager;

import org.bukkit.plugin.java.JavaPlugin;

public class FrameworkManager {
	
	protected JavaPlugin ownerInstance;
	protected FrameworkManagerType type;
	
	public FrameworkManager(FrameworkManagerType type, JavaPlugin ownerInstance) {
		this.type = type;
		this.ownerInstance = ownerInstance;
	}
	
	public JavaPlugin getOwnerInstance() {
		return this.ownerInstance;
	}

	public FrameworkManagerType getType() {
		return this.type;
	}
}