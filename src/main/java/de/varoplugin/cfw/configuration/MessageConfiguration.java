package de.varoplugin.cfw.configuration;

import java.io.File;
import java.util.Objects;

public class MessageConfiguration extends BetterYamlConfiguration {

    public MessageConfiguration(File file) {
        super(file);
    }

    public MessageConfiguration(String name) {
        super(name);
    }

    @Override
    public String getString(String path) {
        return Objects.requireNonNull(super.getString(path)).replace("§", "&")
                .replace("%n%", "\n");
    }
}
