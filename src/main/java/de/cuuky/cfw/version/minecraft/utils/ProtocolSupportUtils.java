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

package de.cuuky.cfw.version.minecraft.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

public class ProtocolSupportUtils {

	// Plugin Name: ProtocolSupport

	private static Method getProtocolVersionMethod, getIdMethod;

	static {
		try {
			getProtocolVersionMethod = Class.forName("protocolsupport.api.ProtocolSupportAPI").getMethod("getProtocolVersion", Player.class);
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {}
	}

	public static int getVersion(Player player) {
		if (getProtocolVersionMethod == null)
			throw new NullPointerException("Could not find ProtocolSupportAPI");

		Object version;
		try {
			version = getProtocolVersionMethod.invoke(null, player);

			if (getIdMethod == null)
				getIdMethod = version.getClass().getMethod("getId");

			return (int) getIdMethod.invoke(version);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static boolean isAvailable() {
		return getProtocolVersionMethod != null;
	}
}