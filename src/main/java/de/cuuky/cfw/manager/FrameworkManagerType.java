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
