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
