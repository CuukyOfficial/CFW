package de.cuuky.cfw.serialize.serializers;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.loader.FieldLoader;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

@Deprecated
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
		List<String> keys = loader.getFields().keySet().stream().collect(Collectors.toList());
		Collections.reverse(keys);

		fieldLoop: for (String saveLocation : keys) {
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

			section.set(saveLocation, value);
			// section.set(saveLocation, value == null ? manager.getNullReplace() : value);
		}
	}
}