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

import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

@Deprecated
public class NumberSerializer extends CFWSerializeType {

	public NumberSerializer(CFWSerializeManager manager) {
		super(manager);
	}

	@Override
	public Object deserialize(CFWSerializeable instance, String key, Field field, ConfigurationSection section) {
		Object object = section.get(key);
		if (field.getType().isAssignableFrom(Long.TYPE) || field.getType().isAssignableFrom(Long.class))
			return ((Number) object).longValue();

		if (field.getType().isAssignableFrom(Short.TYPE) || field.getType().isAssignableFrom(Short.class))
			return ((Number) object).shortValue();

		if (field.getType().isAssignableFrom(Float.TYPE) || field.getType().isAssignableFrom(Float.class))
			return ((Number) object).floatValue();

		if (field.getType().isAssignableFrom(Double.TYPE) || field.getType().isAssignableFrom(Double.class))
			return ((Number) object).doubleValue();

		if (field.getType().equals(Byte.TYPE) || field.getType().isAssignableFrom(Byte.class))
			return ((Number) object).byteValue();

		return null;
	}

	@Override
	public boolean serialize(CFWSerializeable instance, Field field, Object value, String saveUnder, ConfigurationSection section) {
		if (!(value instanceof Long) && !(value instanceof Short) && !(value instanceof Float) && !(value instanceof Double) && !(value instanceof Byte))
			return false;

		section.set(saveUnder, value);
		return true;
	}
}