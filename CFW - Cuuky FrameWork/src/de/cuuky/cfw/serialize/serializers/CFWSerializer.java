package de.cuuky.cfw.serialize.serializers;

import java.lang.reflect.Field;

import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.loader.FieldLoader;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

public class CFWSerializer {

	private CFWSerializeManager manager;
	private ConfigurationSection section;
	private CFWSerializeable serializeable;

	public CFWSerializer(CFWSerializeManager manager, ConfigurationSection section, CFWSerializeable serializeable) {
		this.manager = manager;
		this.section = section;
		this.serializeable = serializeable;
	}

	public void serialize() {
		serializeable.onSerializeStart();
		FieldLoader loader = manager.loadClass(serializeable.getClass());
		System.out.println(serializeable.getClass());
		System.out.println(loader);
		fieldLoop: for (String saveLocation : loader.getFields().keySet()) {
			Field field = loader.getFields().get(saveLocation);
			field.setAccessible(true);
			Object value = null;
			try {
				value = field.get(serializeable);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}

			if (value != null)
				for (CFWSerializeType type : manager.getSerializer())
					if (type.serialize(serializeable, field, value, saveLocation, section))
						continue fieldLoop;

			section.set(saveLocation, value == null ? manager.getNullReplace() : value);
		}
	}
}