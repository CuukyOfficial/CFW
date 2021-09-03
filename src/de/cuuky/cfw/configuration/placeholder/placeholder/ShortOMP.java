package de.cuuky.cfw.configuration.placeholder.placeholder;

import java.util.function.Function;

public class ShortOMP<T> extends ObjectMessagePlaceholder<T> {

    private final Function<T, String> supplier;

    public ShortOMP(String identifier, int refreshDelay, String description, Function<T, String> supplier) {
        super(identifier, refreshDelay, description);

        this.supplier = supplier;
    }

    @Override
    protected String getValue(T player) {
        return this.supplier.apply(player);
    }
}