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

package de.cuuky.cfw.player;

import org.bukkit.scheduler.BukkitRunnable;

import de.cuuky.cfw.configuration.language.LanguageManager;
import de.cuuky.cfw.configuration.language.broadcast.MessageHolder;
import de.cuuky.cfw.configuration.language.languages.LoadableMessage;
import de.cuuky.cfw.configuration.placeholder.MessagePlaceholderManager;

public abstract class CustomLanguagePlayer implements CustomPlayer {

	public MessageHolder sendTranslatedMessage(LoadableMessage message, CustomPlayer replace, MessagePlaceholderManager placeholder, LanguageManager languageManager) {
		MessageHolder holder = new MessageHolder(placeholder);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (getPlayer() == null)
					return;

				getPlayer().sendMessage(holder.getReplaced(languageManager.getMessage(message.getPath(), getLocale()), (replace != null ? replace : CustomLanguagePlayer.this)));
			}
		}.runTaskLater(placeholder.getOwnerInstance(), 1L);

		return holder;
	}
}