package de.cuuky.cfw.configuration.serialization;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SerializationPolicy<T> {

    private final Supplier<T> getter;
    private final Consumer<T> receiver;

    SerializationPolicy(Supplier<T> getter, Consumer<T> receiver) {
        this.getter = getter;
        this.receiver = receiver;
    }

    public T get() {
        return this.getter.get();
    }

    @SuppressWarnings("unchecked")
    public void save(Object value) {
        this.receiver.accept((T) value);
    }
}