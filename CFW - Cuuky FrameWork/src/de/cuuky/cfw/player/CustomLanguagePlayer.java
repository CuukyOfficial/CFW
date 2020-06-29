package de.cuuky.cfw.player;

import de.cuuky.cfw.configuration.language.LanguageManager;
import de.cuuky.cfw.configuration.language.broadcast.MessageHolder;
import de.cuuky.cfw.configuration.language.languages.LoadableMessage;
import de.cuuky.cfw.configuration.placeholder.MessagePlaceholderManager;

public abstract class CustomLanguagePlayer implements CustomPlayer {
	
	public MessageHolder sendTranslatedMessage(LoadableMessage message, CustomPlayer replace,  MessagePlaceholderManager placeholder, LanguageManager languageManager) {
		MessageHolder holder = new MessageHolder(placeholder);
		placeholder.getOwnerInstance().getServer().getScheduler().scheduleSyncDelayedTask(placeholder.getOwnerInstance(), new Runnable() {

			@Override
			public void run() {
				if(getPlayer() == null)
					return;
				
				getPlayer().sendMessage(holder.getReplaced(languageManager.getMessage(message.getPath(), getLocale()), (replace != null ? replace : CustomLanguagePlayer.this)));
			}
		}, 1);

		return holder;
	}
}