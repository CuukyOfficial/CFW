package de.cuuky.cfw.utils;

import org.bukkit.Location;

public class SimpleLocationFormat {

    private final String format;

    public SimpleLocationFormat(String format) {
        this.format = format;
    }

    public String format(Location location) {
        return this.format.replace("%world%", location.getWorld().getName())
            .replace("%x%", String.valueOf(location.getBlockX()))
            .replace("%y%", String.valueOf(location.getBlockY()))
            .replace("%z%", String.valueOf(location.getBlockZ()));
    }
}