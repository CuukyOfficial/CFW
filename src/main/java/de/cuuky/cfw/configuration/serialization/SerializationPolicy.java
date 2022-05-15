package de.cuuky.cfw.configuration.serialization;

import java.util.function.Function;

public class SerializationPolicy<T, C> {

    private final Function<C, T> getter;
    private final Function<T, C> receiver;

    SerializationPolicy(Function<C, T> getter, Function<T, C> receiver) {
        this.getter = getter;
        this.receiver = receiver;
    }

    @SuppressWarnings("unchecked")
    public T serialize(Object from) {
        if (this.getter == null)
            return null;

        return this.getter.apply((C) from);
    }

    @SuppressWarnings("unchecked")
    public C deserialize(Object type) {
        if (this.receiver == null)
            return null;

        return this.receiver.apply((T) type);
    }
}