package de.cuuky.cfw.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import de.cuuky.cfw.utils.JavaUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.Charsets;

public class YamlConfigurationUtil {

	public static YamlConfiguration loadConfiguration(File file) {
        JavaUtils.createFile(file);
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)) {
			return YamlConfiguration.loadConfiguration(reader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void save(YamlConfiguration configuration, File file) {
        JavaUtils.createFile(file);
		try {
			try (OutputStreamWriter oos = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
				oos.write(configuration.saveToString());
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
