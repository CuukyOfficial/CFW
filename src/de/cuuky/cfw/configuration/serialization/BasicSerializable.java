package de.cuuky.cfw.configuration.serialization;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public abstract class BasicSerializable implements ConfigurationSerializable {

    private final Map<String, Field> fields = new HashMap<>();
    private final Map<Class<?>, SerializationPolicy<?, ?>> policies = new HashMap<>();
    private boolean scanned;

    public BasicSerializable() {}

    BasicSerializable(Map<String, Object> data) {
        try {
            this.deserialized(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initSerialization() {
        if (!this.scanned) {
            this.scanFields(this.getClass());
            this.registerPolicies();
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

    private SerializationPolicy<?, ?> prepareField(Field field) {
        field.setAccessible(true);
        return this.policies.get(field.getType());
    }

    private Object getFieldValue(Field field) throws IllegalAccessException {
        SerializationPolicy<?, ?> policy = this.prepareField(field);
        Object value = field.get(this);
        return policy != null ? policy.parse(value) : value;
    }

    private void deserializeField(Field field, Object value) throws IllegalAccessException {
        SerializationPolicy<?, ?> policy = this.prepareField(field);
        field.set(this, policy != null ? policy.parse(value) : value);
    }

    void deserialized(Map<String, Object> data) throws IllegalAccessException {
        this.initSerialization();
        for (String location : this.fields.keySet()) {
            Object value = data.get(location);
            if (value != null)
                this.deserializeField(this.fields.get(location), value);
        }
    }

    protected <P, C> void registerPolicy(Class<C> clazz, SerializationPolicy<P, C> policy) {
        this.policies.put(clazz, policy);
    }

    protected <P, C> void registerPolicy(Class<C> clazz, Function<C, P> supplier, Function<P, C> consumer) {
        this.policies.put(clazz, new SerializationPolicy<>(supplier, consumer));
    }

    /**
     * List of default policies:
     *  - UUID
     */
    protected void registerPolicies() {
        this.registerPolicy(UUID.class, UUID::toString, UUID::fromString);
    }

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
}