package de.cuuky.cfw;

import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.clientadapter.ClientAdapterManager;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import de.cuuky.cfw.player.CustomPlayer;

@Deprecated
public class AdapterCuukyFrameWork<T extends CustomPlayer> extends CuukyFrameWork {

    @Deprecated
	public AdapterCuukyFrameWork(JavaPlugin pluginInstance) {
		super(pluginInstance);
	}

    @Deprecated
	public AdapterCuukyFrameWork(JavaPlugin pluginInstance, FrameworkManager... manager) {
		super(pluginInstance, manager);
	}

    @Deprecated
	@SuppressWarnings("unchecked")
	public ClientAdapterManager<T> getClientAdapterManager() {
		return (ClientAdapterManager<T>) loadManager(FrameworkManagerType.CLIENT_ADAPTER);
	}
}