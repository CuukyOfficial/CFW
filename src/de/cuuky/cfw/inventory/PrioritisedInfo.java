package de.cuuky.cfw.inventory;

import java.util.function.Supplier;

public class PrioritisedInfo {

    private final Info<?> info;
    private final Supplier<Integer> priority;

    public PrioritisedInfo(Supplier<Integer> priority, Info<?> info) {
        this.info = info;
        this.priority = priority;
    }

    public Info<?> getInfo() {
        return info;
    }

    public Supplier<Integer> getPriority() {
        return priority;
    }
}