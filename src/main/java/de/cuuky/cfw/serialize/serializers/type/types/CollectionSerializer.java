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

package de.cuuky.cfw.serialize.serializers.type.types;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.loader.FieldLoader;
import de.cuuky.cfw.serialize.serializers.CFWDeserializer;
import de.cuuky.cfw.serialize.serializers.CFWSerializer;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

@Deprecated
public class CollectionSerializer extends CFWSerializeType {

	public CollectionSerializer(CFWSerializeManager manager) {
		super(manager);
	}

	@Override
	public Object deserialize(CFWSerializeable instance, String key, Field field, ConfigurationSection section) {
		if (!Collection.class.isAssignableFrom(field.getType()) || !section.isConfigurationSection(key))
			return null;

		FieldLoader loader = manager.loadClass(instance.getClass());
		Class<? extends CFWSerializeable> keyClazz = loader.getKeyType(field);
		if (keyClazz == null)
			return null;

		ArrayList<CFWSerializeable> content = new ArrayList<CFWSerializeable>();
		ConfigurationSection arraySection = section.getConfigurationSection(key);
		for (String arrayKey : arraySection.getKeys(true)) {
			Object entry = arraySection.get(arrayKey);
			if (arrayKey.contains("."))
				continue;

			if (Enum.class.isAssignableFrom(keyClazz))
				content.add((CFWSerializeable) manager.deserializeEnum(manager.loadClass(keyClazz), entry));
			else
				content.add(new CFWDeserializer(manager, (ConfigurationSection) entry, instance, keyClazz).deserialize());
		}

		return content;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean serialize(CFWSerializeable instance, Field field, Object value, String saveUnder, ConfigurationSection section) {
		if (!(value instanceof Collection))
			return false;

		FieldLoader loader = manager.loadClass(instance.getClass());
		Class<? extends CFWSerializeable> keyClazz = loader.getKeyType(field);
		if (loader.getKeyType(field) == null)
			return false;

		ArrayList<CFWSerializeable> list = (ArrayList<CFWSerializeable>) value;
		ConfigurationSection listSection = section.createSection(saveUnder);
		for (int i = 0; i < list.size(); i++)
			if (Enum.class.isAssignableFrom(keyClazz))
				listSection.set(String.valueOf(i), manager.serializeEnum(manager.loadClass(keyClazz), list.get(i)));
			else
				new CFWSerializer(manager, listSection.createSection(String.valueOf(i)), list.get(i)).serialize();

		return true;
	}
}