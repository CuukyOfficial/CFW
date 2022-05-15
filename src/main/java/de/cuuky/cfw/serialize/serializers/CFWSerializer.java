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