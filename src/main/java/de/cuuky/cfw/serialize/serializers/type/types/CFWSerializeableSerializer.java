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
import de.cuuky.cfw.serialize.serializers.CFWDeserializer;
import de.cuuky.cfw.serialize.serializers.CFWSerializer;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

@Deprecated
public class CFWSerializeableSerializer extends CFWSerializeType {

    public CFWSerializeableSerializer(CFWSerializeManager manager) {
        super(manager);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(CFWSerializeable instance, String key, Field field, ConfigurationSection section) {
        if (!CFWSerializeable.class.isAssignableFrom(field.getType()) || !section.isConfigurationSection(key) || field.getType().isEnum())
            return null;

        return new CFWDeserializer(manager, section.getConfigurationSection(key), instance, (Class<? extends CFWSerializeable>) field.getType()).deserialize();
    }

    @Override
    public boolean serialize(CFWSerializeable instance, Field field, Object value, String saveLocation, ConfigurationSection section) {
        if (!(value instanceof CFWSerializeable) || field.getType().isEnum())
            return false;

        new CFWSerializer(manager, section.createSection(saveLocation), (CFWSerializeable) value).serialize();
        return true;
    }
}
