package de.cuuky.cfw;

import de.cuuky.cfw.configuration.language.LanguageManager;
import de.cuuky.cfw.configuration.placeholder.MessagePlaceholderManager;
import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import de.cuuky.cfw.menu.SuperInventoryManager;
import de.cuuky.cfw.serialization.CompatibleLocation;
import de.cuuky.cfw.serialize.CFWSerializeManager;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CuukyFrameWork {

	static {
		ConfigurationSerialization.registerClass(CompatibleLocation.class);
	}

	private static final String NAME = "CuukyFrameWork", VERSION = "0.6.7", AUTHOR = "Cuuky";

	/*
	 * CFW - A Bukkit framework
	 * 
	 * CONTACT: { website: "varoplugin.de", discord: 'Cuuky#2783', mail: 'just.cookie.jc@gmail.com' }
	 */

	private final JavaPlugin ownerInstance;
	private final String consolePrefix;
	private final Map<FrameworkManagerType, FrameworkManager> manager = new HashMap<>();

	public CuukyFrameWork(JavaPlugin pluginInstance, FrameworkManager... manager) {
		this.consolePrefix = "[" + pluginInstance.getName() + "] [CFW] ";

		System.out.println(this.consolePrefix + "Loading " + NAME + " v" + VERSION + " by " + AUTHOR + " for plugin " + pluginInstance.getName() + "...");
		this.ownerInstance = pluginInstance;

		for (FrameworkManager fm : manager) {
			System.out.println(this.consolePrefix + "Using Custom-Manager " + fm.getClass().getName() + "!");
			this.manager.put(fm.getType(), fm);
		}
	}

	public CuukyFrameWork(JavaPlugin pluginInstance) {
		this(pluginInstance, new FrameworkManager[0]);
	}

	protected FrameworkManager loadManager(FrameworkManagerType t) {
		return this.manager.computeIfAbsent(t, type -> {
			try {
				return type.getManager().getDeclaredConstructor(JavaPlugin.class).newInstance(this.ownerInstance);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
				throw new IllegalStateException(this.consolePrefix + "Failed to initialize type " + type + "!");
			}
		});
	}

	public void disable() {
		this.manager.values().forEach(FrameworkManager::disable);
	}

	public JavaPlugin getPluginInstance() {
		return this.ownerInstance;
	}

	public HookManager getHookManager() {
		return (HookManager) loadManager(FrameworkManagerType.HOOKING);
	}

	@Deprecated
	public SuperInventoryManager getInventoryManager() {
		return (SuperInventoryManager) loadManager(FrameworkManagerType.INVENTORY);
	}

	public AdvancedInventoryManager getAdvancedInventoryManager() {
		return (AdvancedInventoryManager) loadManager(FrameworkManagerType.ADVANCED_INVENTORY);
	}

	public LanguageManager getLanguageManager() {
		return (LanguageManager) loadManager(FrameworkManagerType.LANGUAGE);
	}

	public MessagePlaceholderManager getPlaceholderManager() {
		return (MessagePlaceholderManager) loadManager(FrameworkManagerType.PLACEHOLDER);
	}

	public CFWSerializeManager getSerializeManager() {
		return (CFWSerializeManager) loadManager(FrameworkManagerType.SERIALIZE);
	}
}