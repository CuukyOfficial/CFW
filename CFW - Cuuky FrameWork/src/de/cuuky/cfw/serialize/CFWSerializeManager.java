package de.cuuky.cfw.serialize;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import de.cuuky.cfw.CuukyFrameWork;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import de.cuuky.cfw.serialize.identifier.CFWSerializeField;
import de.cuuky.cfw.serialize.identifier.CFWSerializeable;

public class CFWSerializeManager extends FrameworkManager {

	protected final String nullReplace;
	protected Map<String, YamlConfiguration> configs;
	protected Map<String, File> files;

	protected Map<CFWSerializeable, String> enumsRepl;
	protected List<CFWSerializeObject> handler;

	public CFWSerializeManager(FrameworkManagerType type, CuukyFrameWork framework) {
		super(type, framework);

		nullReplace = "nullReplace";
		handler = new ArrayList<CFWSerializeObject>();
		enumsRepl = new HashMap<CFWSerializeable, String>();
		configs = new HashMap<String, YamlConfiguration>();
		files = new HashMap<String, File>();
	}

	public File getFile(String name) {
		return this.files.get(name);
	}

	public void addFile(String name, File file) {
		this.files.put(name, file);
	}

	public YamlConfiguration getConfiguration(String name) {
		return this.configs.get(name);
	}

	public void addConfiguration(String name, YamlConfiguration configuration) {
		this.configs.put(name, configuration);
	}

	public CFWSerializeable getEnumByString(String en) {
		for (CFWSerializeable var : enumsRepl.keySet())
			if (en.equals(enumsRepl.get(var)))
				return var;

		return null;
	}

	public void addHandler(CFWSerializeObject handler) {
		this.handler.add(handler);
	}

	public CFWSerializeObject getHandler(Class<?> clazz) {
		for (CFWSerializeObject handl : handler)
			if (handl.getClazz().equals(clazz))
				return handl;

		return null;
	}

	public String getStringByEnum(CFWSerializeable ser) {
		for (CFWSerializeable var : enumsRepl.keySet())
			if (ser.equals(var))
				return enumsRepl.get(var);

		return null;
	}

	public void registerClass(Class<? extends CFWSerializeable> clazz) {
		new CFWSerializeObject(this, clazz);
	}

	public void registerEnum(Class<? extends CFWSerializeable> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			CFWSerializeField anno = field.getAnnotation(CFWSerializeField.class);
			if (anno == null)
				continue;

			try {
				enumsRepl.put((CFWSerializeable) field.get(clazz), anno.enumValue());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveAll() {
		for (CFWSerializeObject handl : handler)
			if (handl.getConfiguration() != null)
				handl.onSave();
	}

	public String getNullReplace() {
		return nullReplace;
	}
}