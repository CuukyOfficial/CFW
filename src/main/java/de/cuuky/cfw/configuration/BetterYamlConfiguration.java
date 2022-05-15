package de.cuuky.cfw.configuration;

import com.google.common.base.Charsets;
import de.cuuky.cfw.utils.JavaUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class BetterYamlConfiguration extends YamlConfiguration {

    private interface ThrowingConsumer<T> {
        void accept(T type) throws IOException, InvalidConfigurationException;
    }

    private final File file;

    public BetterYamlConfiguration(File file) {
        this.file = file;
        this.doFileOperation(this::loadParsed);
    }

    public BetterYamlConfiguration(String name) {
        this(new File(name));
    }

    private void loadParsed(File file) throws InvalidConfigurationException {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)) {
            this.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doFileOperation(ThrowingConsumer<File> operation) {
        try {
            JavaUtils.createFile(this.file);
            operation.accept(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        this.doFileOperation(this::save);
    }

    public File getFile() {
        return this.file;
    }
}