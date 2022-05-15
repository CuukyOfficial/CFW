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

public class MagmaVersionAdapter extends OneTwelveVersionAdapter {

	@Override
	protected void initXp(String entityHumanName, String foodMetaName) {
		// this is EXTREMELY unsafe
		try {
			Class<?> foodMetaClass = Class.forName(foodMetaName);
			int fieldNum = 0;
			for (Field field : Class.forName(entityHumanName).getDeclaredFields())
				if (fieldNum == 0 && field.getType() == foodMetaClass)
					fieldNum = 1;
				else if (fieldNum == 1 && field.getType() == foodMetaClass)
					fieldNum = 2;
				else if (fieldNum == 2 && field.getType() == int.class)
					fieldNum = 3;
				else if (fieldNum == 3 && field.getType() == float.class)
					fieldNum = 4;
				else if (fieldNum == 4 && field.getType() == float.class)
					fieldNum = 5;
				else if (fieldNum == 5 && field.getType() == int.class) {
					this.xpCooldownField = field;
					return;
				} else
					fieldNum = 0;

			throw new Error("Unable to find xp cooldown field");
		} catch (SecurityException | ClassNotFoundException e) {
			throw new Error(e);
		}
	}
}
