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

package de.cuuky.cfw.serialize.loader;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import de.cuuky.cfw.serialize.identifiers.CFWSerializeField;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.identifiers.NullClass;
import de.cuuky.cfw.utils.JavaUtils;

@Deprecated
public class FieldLoader {

    private Map<Field, Class<? extends CFWSerializeable>> keyTypes, valueTypes;
    private Map<String, Field> fields;

    private Class<?> clazz;

    public FieldLoader(Class<?> clazz) {
        this.fields = new LinkedHashMap<>();
        this.keyTypes = new LinkedHashMap<>();
        this.valueTypes = new LinkedHashMap<>();
        this.clazz = clazz;

        loadFields();
    }

    private void loadFields() {
        Field[] declFields = clazz.getDeclaredFields();
        for (Field field : declFields) {
            CFWSerializeField anno = field.getAnnotation(CFWSerializeField.class);
            if (anno == null)
                continue;

            String path = anno.path();
            fields.put(path.equals("PATH") ? anno.enumValue() : path, field);
            if (anno.keyClass() != NullClass.class)
                keyTypes.put(field, anno.keyClass());

            if (anno.valueClass() != NullClass.class)
                valueTypes.put(field, anno.valueClass());
        }

        this.reverseMaps();
    }

    private void reverseMaps() {
        this.fields = JavaUtils.reverseMap(this.fields);
        this.keyTypes = JavaUtils.reverseMap(this.keyTypes);
        this.valueTypes = JavaUtils.reverseMap(this.valueTypes);
    }

    public Class<? extends CFWSerializeable> getKeyType(Field field) {
        return this.keyTypes.get(field);
    }

    public Class<? extends CFWSerializeable> getValueType(Field field) {
        return this.valueTypes.get(field);
    }

    public Map<String, Field> getFields() {
        return fields;
    }
}
