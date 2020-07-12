package de.cuuky.cfw;

import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.clientadapter.ClientAdapterManager;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import de.cuuky.cfw.player.CustomPlayer;

public class AdapterCuukyFrameWork<T extends CustomPlayer> extends CuukyFrameWork {

	public AdapterCuukyFrameWork(JavaPlugin pluginInstance) {
		super(pluginInstance);
	}
	
	public AdapterCuukyFrameWork(JavaPlugin pluginInstance, FrameworkManager... manager) {
		super(pluginInstance, manager);
	}
	
	@SuppressWarnings("unchecked")
	public ClientAdapterManager<T> getClientAdapterManager() {
		return (ClientAdapterManager<T>) loadManager(FrameworkManagerType.CLIENT_ADAPTER);
	}
}