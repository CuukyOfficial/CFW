package de.cuuky.cfw.serialize.serializers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.loader.FieldLoader;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

@Deprecated
public class CFWDeserializer {

	private CFWSerializeManager manager;
	private ConfigurationSection section;
	private Object parent;
	private Class<? extends CFWSerializeable> clazz;

	public CFWDeserializer(CFWSerializeManager manager, ConfigurationSection section, Object parent, Class<? extends CFWSerializeable> clazz) {
		this.manager = manager;
		this.section = section;
		this.parent = parent;
		this.clazz = clazz;
	}

	public CFWSerializeable deserialize() {
		CFWSerializeable instance = null;
		FieldLoader loader = manager.loadClass(clazz);

		if (parent != null)
			try {
				instance = clazz.getConstructor(parent.getClass()).newInstance(parent);
			} catch (NullPointerException | IllegalArgumentException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException e) {

			}

		if (instance == null)
			try {
				instance = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {
				e1.printStackTrace();
				return null;
			}

		for (String sections : this.section.getKeys(true)) {
			Field field = loader.getFields().get(sections);
			if (field == null)
				continue;

			field.setAccessible(true);
			Object obj = section.get(sections);
			// if (obj instanceof String && ((String)
			// obj).equals("nullReplace"))
			// obj = null;
			// else
			for (CFWSerializeType type : manager.getSerializer()) {
				Object get = type.deserialize(instance, sections, field, section);
				if (get == null)
					continue;

				obj = get;
				break;
			}

			try {
				field.set(instance, obj);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		instance.onDeserializeEnd();
		return instance;
	}
}