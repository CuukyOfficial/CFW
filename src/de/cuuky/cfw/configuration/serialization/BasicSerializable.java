package de.cuuky.cfw.configuration.serialization;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class BasicSerializable implements ConfigurationSerializable {

    private final Map<String, Field> fields = new HashMap<>();
    private final Map<Class<?>, SerializationPolicy<?>> policies = new HashMap<>();
    private boolean scanned;

    private void initSerialization() {
        if (!this.scanned) {
            this.scanFields(this.getClass());
            this.registerSerializes();
            this.scanned = true;
        }
    }

    private void scanFields(Class<?> clazz) {
        this.checkFields(clazz.getFields());
        this.checkFields(clazz.getDeclaredFields());
        if (BasicSerializable.class.isAssignableFrom(clazz.getSuperclass()))
            this.scanFields(clazz.getSuperclass());
    }

    private void checkFields(Field[] fields) {
        for (Field field : fields) {
            Serialize info = field.getAnnotation(Serialize.class);
            if (info != null)
                this.fields.put(info.value(), field);
        }
    }

    private SerializationPolicy<?> prepareField(Field field) {
        field.setAccessible(true);
        return this.policies.get(field.getType());
    }

    private Object getFieldValue(Field field) throws IllegalAccessException {
        SerializationPolicy<?> policy = this.prepareField(field);
        return policy != null ? policy.get() : field.get(this);
    }

    private void deserializeField(Field field, Object value) throws IllegalAccessException {
        SerializationPolicy<?> policy = this.prepareField(field);
        if (policy != null) {
            policy.save(value);
        } else {
            field.set(this, value);
        }
    }

    void deserialized(Map<String, Object> data) throws IllegalAccessException {
        this.initSerialization();
        for (String location : this.fields.keySet()) {
            Object value = data.get(location);
            if (value != null)
                this.deserializeField(this.fields.get(location), value);
        }
    }

    protected <P> void registerSerialize(Class<?> clazz, Supplier<P> supplier, Consumer<P> consumer) {
        this.policies.put(clazz, new SerializationPolicy<>(supplier, consumer));
    }

    protected void registerSerializes() {}

    @Override
    public Map<String, Object> serialize() {
        this.initSerialization();
        Map<String, Object> data = new LinkedHashMap<>();
        for (String loc : this.fields.keySet()) {
            try {
                data.put(loc, this.getFieldValue(this.fields.get(loc)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static <T extends BasicSerializable> T computeData(Map<String, Object> data, Class<T> clazz) {
        T object = null;
        try {
            object = new Instantiator<>(clazz).instantiate();
            object.deserialized(data);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return object;
    }
}