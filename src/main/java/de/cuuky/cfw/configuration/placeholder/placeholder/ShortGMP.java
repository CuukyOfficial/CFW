package de.cuuky.cfw.configuration.placeholder.placeholder;

import java.util.function.Supplier;

public class ShortGMP extends GeneralMessagePlaceholder {

    private final Supplier<String> supplier;

    public ShortGMP(String identifier, int refreshDelay, boolean rawIdentifier, String description, Supplier<String> supplier) {
        super(identifier, refreshDelay, rawIdentifier, description);

        this.supplier = supplier;
    }

    public ShortGMP(String identifier, int refreshDelay, String description, Supplier<String> supplier) {
        this(identifier, refreshDelay, false, description, supplier);
    }

    @Override
    protected String getValue() {
        return supplier.get();
    }
}