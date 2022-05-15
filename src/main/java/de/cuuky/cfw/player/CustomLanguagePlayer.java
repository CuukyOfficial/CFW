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