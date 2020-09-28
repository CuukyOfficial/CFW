package de.cuuky.cfw.configuration.language;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.configuration.language.broadcast.MessageHolder;
import de.cuuky.cfw.configuration.language.languages.LoadableMessage;
import de.cuuky.cfw.configuration.placeholder.MessagePlaceholderManager;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import de.cuuky.cfw.player.CustomLanguagePlayer;
import de.cuuky.cfw.player.CustomPlayer;

public class LanguageManager extends FrameworkManager {

	private String languagePath, fallbackLocale;
	private Language defaultLanguage;
	private Map<String, Language> languages;
	private Map<String, String> defaultMessages;

	public LanguageManager(JavaPlugin instance) {
		this("plugins/" + instance.getName() + "/languages/", "en_us", instance);
	}

	public LanguageManager(String languagesPath, String fallbackLocale, JavaPlugin instance) {
		super(FrameworkManagerType.LANGUAGE, instance);

		this.languagePath = languagesPath;
		this.fallbackLocale = fallbackLocale;
		this.languages = new HashMap<>();
		this.defaultMessages = new HashMap<>();
	}

	public String getMessage(String messagePath, String locale) {
		if (locale == null)
			return defaultLanguage.getMessage(messagePath);
		else {
			Language language = languages.get(locale);
			String message = null;

			if (language == null)
				message = (language = defaultLanguage).getMessage(messagePath);
			else {
				message = language.getMessage(messagePath);

				if (message == null)
					message = (language = defaultLanguage).getMessage(messagePath);
			}

			if (message == null)
				message = languages.get(fallbackLocale).getMessage(messagePath);

			return message;
		}
	}

	public Language registerLanguage(String name) {
		return registerLoadableLanguage(name, null);
	}

	public Language registerLoadableLanguage(String name, Class<? extends LoadableMessage> clazz) {
		Language language = null;
		languages.put(name, language = new Language(name, this, clazz));

		return language;
	}

	public void setDefaultLanguage(Language defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
		this.defaultMessages = getValues(defaultLanguage.getClazz());

		this.defaultLanguage.load();
		for (Language lang : this.languages.values())
			if (!lang.isLoaded() && !lang.getFile().exists())
				lang.load();
	}

	public HashMap<String, String> getValues(Class<? extends LoadableMessage> clazz) {
		HashMap<String, String> values = new HashMap<>();
		LoadableMessage[] messages = null;

		try {
			messages = (LoadableMessage[]) clazz.getMethod("values").invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}

		for (LoadableMessage lm : messages)
			values.put(lm.getPath(), lm.getDefaultMessage());

		return values;
	}

	public void loadLanguages() {
		File file = new File(languagePath);
		if (!file.isDirectory())
			file.mkdir();

		for (File listFile : file.listFiles()) {
			if (!listFile.getName().endsWith(".yml") || languages.containsKey(listFile.getName().replace(".yml", "")))
				continue;

			registerLanguage(listFile.getName().replace(".yml", ""));
		}
	}

	public MessageHolder broadcastMessage(LoadableMessage message, CustomPlayer replace, MessagePlaceholderManager placeholderManager, ArrayList<CustomLanguagePlayer> players) {
		MessageHolder holder = new MessageHolder(placeholderManager);
		this.ownerInstance.getServer().getScheduler().scheduleSyncDelayedTask(ownerInstance, new Runnable() {

			@Override
			public void run() {
				ownerInstance.getServer().getConsoleSender().sendMessage(holder.getReplaced(getMessage(message.getPath(), defaultLanguage.getName()), replace));
				for (CustomLanguagePlayer player : players)
					if (player.getPlayer() != null)
						player.getPlayer().sendMessage(holder.getReplaced(getMessage(message.getPath(), ((CustomPlayer) player).getLocale()), replace != null ? replace : (CustomPlayer) player));
			}
		}, 1);

		return holder;
	}

	public String getLanguagePath() {
		return this.languagePath;
	}

	public Language getDefaultLanguage() {
		return this.defaultLanguage;
	}

	public Map<String, String> getDefaultMessages() {
		return this.defaultMessages;
	}

	public Map<String, Language> getLanguages() {
		return this.languages;
	}
}