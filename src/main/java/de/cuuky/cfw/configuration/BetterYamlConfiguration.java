/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cuuky.cfw.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.Charsets;

import de.cuuky.cfw.utils.JavaUtils;

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
