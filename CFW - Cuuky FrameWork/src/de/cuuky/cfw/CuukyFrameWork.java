package de.cuuky.cfw;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.clientadapter.ClientAdapterManager;
import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.hooking.listener.HookListener;
import de.cuuky.cfw.menu.SuperInventoryManager;
import de.cuuky.cfw.menu.utils.InventoryListener;
import de.cuuky.cfw.serialization.CompatibleLocation;

public class CuukyFrameWork {

	/*
	 * CFW - A Bukkit framework
	 * 
	 * VERSION: 0.1 (2020) 
	 * AUTHOR: Cuuky
	 * CONTACT: { website: "varoplugin.de", discord: 'Cuuky#2783', mail: 'just.cookie.jc@gmail.com' }
	 */

	private JavaPlugin ownerInstance;

	private HookManager hookManager;
	private ClientAdapterManager clientAdapterManager;
	private SuperInventoryManager inventoryManager;

	public CuukyFrameWork(JavaPlugin pluginInstance) {
		this.ownerInstance = pluginInstance;

		this.hookManager = new HookManager(this);
		this.clientAdapterManager = new ClientAdapterManager(this);
		this.inventoryManager = new SuperInventoryManager(this);
		
		ConfigurationSerialization.registerClass(CompatibleLocation.class);

		registerListener();
	}

	private void registerListener() {
		Bukkit.getPluginManager().registerEvents(new HookListener(this.hookManager), ownerInstance);
		Bukkit.getPluginManager().registerEvents(new InventoryListener(this.inventoryManager), ownerInstance);
	}

	public JavaPlugin getPluginInstance() {
		return this.ownerInstance;
	}

	public HookManager getHookManager() {
		return hookManager;
	}

	public ClientAdapterManager getClientAdapterManager() {
		return clientAdapterManager;
	}

	public SuperInventoryManager getInventoryManager() {
		return inventoryManager;
	}
}