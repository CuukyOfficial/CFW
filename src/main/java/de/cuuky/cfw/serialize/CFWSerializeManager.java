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

package de.cuuky.cfw.serialize;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.configuration.YamlConfigurationUtil;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.loader.FieldLoader;
import de.cuuky.cfw.serialize.serializers.CFWDeserializer;
import de.cuuky.cfw.serialize.serializers.CFWSerializer;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;
import de.cuuky.cfw.serialize.serializers.type.types.CFWSerializeableSerializer;
import de.cuuky.cfw.serialize.serializers.type.types.CollectionSerializer;
import de.cuuky.cfw.serialize.serializers.type.types.EnumSerializer;
import de.cuuky.cfw.serialize.serializers.type.types.LocationSerializer;
import de.cuuky.cfw.serialize.serializers.type.types.MapSerializer;
import de.cuuky.cfw.serialize.serializers.type.types.NumberSerializer;

@Deprecated
public class CFWSerializeManager extends FrameworkManager {

    private final Map<Class<?>, FieldLoader> loaded;

    private final List<CFWSerializeType> serializer;

    public CFWSerializeManager(JavaPlugin instance) {
        super(FrameworkManagerType.SERIALIZE, instance);

        this.loaded = new LinkedHashMap<>();
        this.serializer = new ArrayList<>();

        this.serializer.add(new CFWSerializeableSerializer(this));
        this.serializer.add(new MapSerializer(this));
        this.serializer.add(new NumberSerializer(this));
        this.serializer.add(new LocationSerializer(this));
        this.serializer.add(new EnumSerializer(this));
        this.serializer.add(new CollectionSerializer(this));
    }

    public void saveSerializeable(String key, CFWSerializeable serializeable, YamlConfiguration configuration) {
        new CFWSerializer(this, configuration.createSection(key), serializeable).serialize();
    }

    public <T> List<T> loadSerializeables(Class<T> clazz, File file) {
        return this.loadSerializeables(clazz, file, null);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> loadSerializeables(Class<T> clazz, File file, Object invokeObject) {
        List<T> serializeables = new ArrayList<>();
        YamlConfiguration configuration = YamlConfigurationUtil.loadConfiguration(file);

        for (String s : configuration.getKeys(true)) {
            if (!configuration.isConfigurationSection(s) || s.contains("."))
                continue;

            serializeables.add((T) new CFWDeserializer(this, configuration.getConfigurationSection(s), invokeObject, (Class<? extends CFWSerializeable>) clazz).deserialize());
        }

        return serializeables;
    }

    public <T> void saveFiles(Class<T> clazz, List<T> list, File file, SaveVisit<T> visit) {
        if (file.exists())
            file.delete();

        YamlConfiguration configuration = YamlConfigurationUtil.loadConfiguration(file);

        for (T serializeable : list)
            new CFWSerializer(this, configuration.createSection(visit.onKeySave(serializeable)), (CFWSerializeable) serializeable).serialize();

        YamlConfigurationUtil.save(configuration, file);
    }

    public FieldLoader loadClass(Class<?> clazz) {
        if (loaded.containsKey(clazz))
            return loaded.get(clazz);
        else {
            FieldLoader loader = new FieldLoader(clazz);
            this.loaded.put(clazz, loader);
            return loader;
        }
    }

    /*
     * Needs a recode rofl
     */
    public String serializeEnum(FieldLoader loader, Object object) {
        for (String s : loader.getFields().keySet()) {
            Object enumValue = null;
            try {
                enumValue = loader.getFields().get(s).get(null);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            if (enumValue.equals(object))
                return s;
        }

        return null;
    }

    public Object deserializeEnum(FieldLoader loader, Object object) {
        try {
            return loader.getFields().get(object).get(null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<CFWSerializeType> getSerializer() {
        return serializer;
    }

    // public String getNullReplace() {
    // return "nullReplace";
    // }

    public static abstract class SaveVisit<T> {

        public abstract String onKeySave(T object);

    }
}
