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
import de.cuuky.cfw.serialize.loader.FieldLoader;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

@Deprecated
public class EnumSerializer extends CFWSerializeType {

    public EnumSerializer(CFWSerializeManager manager) {
        super(manager);
    }

    @Override
    public Object deserialize(CFWSerializeable instance, String key, Field field, ConfigurationSection section) {
        Object object = section.get(key);
        if (!field.getType().isEnum() || !(object instanceof String) || !CFWSerializeable.class.isAssignableFrom(field.getType()))
            return null;

        @SuppressWarnings("unchecked")
        FieldLoader loader = manager.loadClass(field.getType());
        return manager.deserializeEnum(loader, object);
        // try {
        // return loader.getFields().get((String) object).get(null);
        // } catch (IllegalArgumentException | IllegalAccessException e) {
        // e.printStackTrace();
        // }
        //
        // return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean serialize(CFWSerializeable instance, Field field, Object value, String saveUnder, ConfigurationSection section) {
        if (!field.getType().isEnum() || !CFWSerializeable.class.isAssignableFrom(field.getType()))
            return false;

        FieldLoader loader = manager.loadClass(field.getType());
        section.set(saveUnder, manager.serializeEnum(loader, value));
        return true;
    }
}
