package de.cuuky.cfw.manager;

import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.CuukyFrameWork;

public class FrameworkManager {
	
	protected JavaPlugin ownerInstance;
	protected CuukyFrameWork<?> framework;
	protected FrameworkManagerType type;
	
	public FrameworkManager(FrameworkManagerType type, CuukyFrameWork<?> framework) {
		this.type = type;
		this.framework = framework;
		this.ownerInstance = framework.getPluginInstance();
	}
	
	public JavaPlugin getOwnerInstance() {
		return this.ownerInstance;
	}

	public FrameworkManagerType getType() {
		return this.type;
	}
	
	public CuukyFrameWork<?> getFramework() {
		return framework;
	}
}