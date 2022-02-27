package de.cuuky.cfw.manager;

import de.cuuky.cfw.clientadapter.ClientAdapterManager;
import de.cuuky.cfw.configuration.language.LanguageManager;
import de.cuuky.cfw.configuration.placeholder.MessagePlaceholderManager;
import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.player.LanguagePlayerManager;
import de.cuuky.cfw.serialize.CFWSerializeManager;

public enum FrameworkManagerType {

	PLACEHOLDER(MessagePlaceholderManager.class),
	LANGUAGE(LanguageManager.class),
	ADVANCED_INVENTORY(AdvancedInventoryManager.class),
	HOOKING(HookManager.class),
    @Deprecated
	CLIENT_ADAPTER(ClientAdapterManager.class),
	PLAYER(LanguagePlayerManager.class),
    @Deprecated
	SERIALIZE(CFWSerializeManager.class);

	private Class<? extends FrameworkManager> manager;

	FrameworkManagerType(Class<? extends FrameworkManager> manager) {
		this.manager = manager;
	}

	public Class<? extends FrameworkManager> getManager() {
		return this.manager;
	}
}