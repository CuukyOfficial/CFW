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

package de.varoplugin.cfw.version;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class OneFourteenVersionAdapter extends OneThirteenVersionAdapter {

    @Override
    protected void initServerProperties() throws SecurityException {
        // TODO implement this
    }

    @Override
    public Properties getServerProperties() {
        try {
            Class<?> mcServerClass = Class.forName(VersionUtils.getNmsClass() + ".MinecraftServer");
            Object mcServer = mcServerClass.getMethod("getServer").invoke(null);

            Field serverSettingsField = mcServer.getClass().getField("propertyManager");
            serverSettingsField.setAccessible(true);
            Object serverSettings = serverSettingsField.get(mcServer);

            Field serverPropertiesField = serverSettings.getClass().getDeclaredField("properties");
            serverPropertiesField.setAccessible(true);
            Object serverProperties = serverPropertiesField.get(serverSettings);

            Field propertiesField = serverProperties.getClass().getField("properties");
            propertiesField.setAccessible(true);

            return (Properties) propertiesField.get(serverProperties);
        } catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | InvocationTargetException | NoSuchMethodException e) {
            throw new Error(e);
        }
    }
}
