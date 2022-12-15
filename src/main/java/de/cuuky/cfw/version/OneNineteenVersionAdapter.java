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

package de.cuuky.cfw.version;

import java.lang.reflect.Field;
import java.util.Collections;

import org.bukkit.Bukkit;

public class OneNineteenVersionAdapter extends OneSeventeenVersionAdapter {

    @Override
    public void forceClearWorlds() {
        try {
            Field dedicatedServerField = Class.forName("org.bukkit.craftbukkit." + VersionUtils.getNmsVersion() + ".CraftServer").getDeclaredField("console");
            dedicatedServerField.setAccessible(true);
            Object dedicatedServer = dedicatedServerField.get(Bukkit.getServer());
            Field worldServerField = dedicatedServer.getClass().getSuperclass().getDeclaredField(this.getWorldServerFieldName());
            worldServerField.setAccessible(true);
            worldServerField.set(dedicatedServer, Collections.emptyMap());
        } catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new Error(e);
        }
    }

    @Override
    protected String getWorldServerFieldName() {
        return "O";
    }
}
