/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cuuky.cfw;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.configuration.language.LanguageManager;
import de.cuuky.cfw.configuration.placeholder.MessagePlaceholderManager;
import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import de.cuuky.cfw.serialization.CompatibleLocation;
import de.cuuky.cfw.serialize.CFWSerializeManager;

public class CuukyFrameWork {

	static {
		ConfigurationSerialization.registerClass(CompatibleLocation.class);
	}

	private static final String NAME = "CuukyFrameWork", VERSION = "0.6.11", AUTHOR = "Cuuky";

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