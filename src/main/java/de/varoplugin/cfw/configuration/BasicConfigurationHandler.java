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

package de.varoplugin.cfw.configuration;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class BasicConfigurationHandler {

    private final BetterYamlConfiguration configuration;

    public BasicConfigurationHandler(String path) {
        this.configuration = new BetterYamlConfiguration(path);
        this.configuration.options().copyDefaults(true);
        if (!this.configuration.getFile().exists())
            this.save();
    }

    public void save() {
        this.configuration.save();
    }

    public void setValue(String path, Object object) {
        this.configuration.set(path, object);
    }

    public Object getValue(String name, Object defaultValue) {
        if (this.configuration.contains(name))
            return this.configuration.get(name);

        this.configuration.addDefault(name, defaultValue);
        this.save();
        return defaultValue;
    }

    public boolean getBool(String name, boolean defaultValue) {
        return (boolean) getValue(name, defaultValue);
    }

    public String getString(String name, String defaultValue) {
        return (String) getValue(name, defaultValue);
    }

    public String getColoredString(String name, String defaultValue) {
        return ChatColor.translateAlternateColorCodes('&', getString(name, defaultValue));
    }

    public int getInt(String name, int defaultValue) {
        return (Integer) getValue(name, defaultValue);
    }

    public YamlConfiguration getYamlConfig() {
        return this.configuration;
    }
}
