package de.cuuky.cfw.configuration.language;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import de.cuuky.cfw.configuration.language.languages.LoadableMessage;

public class LanguageManager {

	private String languagePath, fallbackLocale;
	private Language defaultLanguage;
	private HashMap<String, Language> languages;
	private HashMap<String, String> defaultMessages;

	public LanguageManager(String languagesPath, String fallbackLocale) {
		this.languagePath = languagesPath;
		this.fallbackLocale = fallbackLocale;
		this.languages = new HashMap<>();
		this.defaultMessages = new HashMap<>();
	}

	protected String getMessage(String messagePath, String locale) {
		if(locale == null)
			return defaultLanguage.getMessage(messagePath);
		else {
			Language language = languages.get(locale);
			String message = null;

			if(language == null)
				message = (language = defaultLanguage).getMessage(messagePath);
			else {
				message = language.getMessage(messagePath);

				if(message == null)
					message = (language = defaultLanguage).getMessage(messagePath);
			}

			if(message == null)
				message = languages.get(fallbackLocale).getMessage(messagePath);

			return message;
		}
	}

	protected Language registerLanguage(String name) {
		return registerDefaultLanguage(name, null);
	}

	protected Language registerDefaultLanguage(String name, Class<? extends LoadableMessage> clazz) {
		Language language = null;
		languages.put(name, language = new Language(name, this, clazz));

		return language;
	}

	protected void setDefaultLanguage(Language defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
		this.defaultMessages = getValues(defaultLanguage.getClazz());
	}
	
	protected HashMap<String, String> getValues(Class<? extends LoadableMessage> clazz) {
		HashMap<String, String> values = new HashMap<>();
		LoadableMessage[] messages = null;
		
		try {
			messages = (LoadableMessage[]) clazz.getMethod("values").invoke(null);
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}

		for(LoadableMessage lm : messages) 
			values.put(lm.getPath(), lm.getDefaultMessage());
		
		return values;
	}

	public void loadLanguages() {
		File file = new File(languagePath);
		for(File listFile : file.listFiles()) {
			if(!listFile.getName().endsWith(".yml") || languages.containsKey(listFile.getName().replace(".yml", "")))
				continue;

			registerLanguage(listFile.getName().replace(".yml", ""));
		}
	}

	public String getLanguagePath() {
		return this.languagePath;
	}

	public Language getDefaultLanguage() {
		return this.defaultLanguage;
	}

	public HashMap<String, String> getDefaultMessages() {
		return this.defaultMessages;
	}

	public HashMap<String, Language> getLanguages() {
		return this.languages;
	}
}