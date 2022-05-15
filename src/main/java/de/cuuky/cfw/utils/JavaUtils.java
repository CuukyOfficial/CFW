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

package de.cuuky.cfw.utils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;

public final class JavaUtils {

	public static ArrayList<String> addIntoEvery(ArrayList<String> input, String into, boolean start) {
		for (int i = 0; i < input.size(); i++)
			input.set(i, (start ? into + input.get(i) : input.get(i) + into));

		return input;
	}

	public static String[] addIntoEvery(String[] input, String into, boolean start) {
		for (int i = 0; i < input.length; i++)
			input[i] = (start ? into + input[i] : input[i] + into);

		return input;
	}

	public static String[] arrayToCollection(List<String> strings) {
		return strings.toArray(new String[0]);
	}

	public static ArrayList<String> collectionToArray(String[] strings) {
		ArrayList<String> newStrings = new ArrayList<>();
		Collections.addAll(newStrings, strings);
		return newStrings;
	}

	public static String[] combineArrays(String[]... strings) {
		ArrayList<String> string = new ArrayList<>();

		for (String[] ss : strings)
			Collections.addAll(string, ss);

		return getAsArray(string);
	}

	public static String getArgsToString(ArrayList<String> args, String insertBewteen) {
		return String.join(insertBewteen, args);
	}

	public static String getArgsToString(String[] args, String insertBewteen) {
		return String.join(insertBewteen, args);
	}

	public static String[] getAsArray(ArrayList<String> string) {
		return string.toArray(new String[0]);
	}

	public static ArrayList<Object> getAsList(String[] lis) {
		return new ArrayList<>(Arrays.asList(lis));
	}

	public static int getNextToNine(int to) {
		int mod = to % 9;
		int result = (mod != 0 ? (9 - mod) : mod) + to;
		return Math.min(result, 54);
	}

	public static Object getStringObject(String obj) {
		try {
			return Integer.parseInt(obj);
		} catch (NumberFormatException e) {
		}

		try {
			return Long.parseLong(obj);
		} catch (NumberFormatException e) {
		}

		try {
			return Double.parseDouble(obj);
		} catch (NumberFormatException e) {
		}

		if (obj.equalsIgnoreCase("true") || obj.equalsIgnoreCase("false"))
			return obj.equalsIgnoreCase("true");
		else
			return obj;
	}

	/**
	 * @param min The minimum Range
	 * @param max The maximum Range
	 * @return Returns a random Integer between the min and the max range
	 */
	public static int randomInt(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

	public static String[] removeString(String[] string, int loc) {
		String[] ret = new String[string.length - 1];
		int i = 0;
		boolean removed = false;
		for (String arg : string) {
			if (i == loc && !removed) {
				removed = true;
				continue;
			}

			ret[i] = arg;
			i++;
		}

		return ret;
	}

	public static String replaceAllColors(String s) {
		return ChatColor.stripColor(s.replace("§", "&"));
	}

	public static String getCurrentDateAsFileable() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static boolean createFile(File file) {
		if (file.exists())
			return true;

		try {
			File parent = new File(file.getParent());
			parent.mkdirs();
			return file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void deleteDirectory(File file) {
		for (File listFile : file.listFiles()) {
			if (listFile.isDirectory())
				deleteDirectory(listFile);

			listFile.delete();
		}

		file.delete();
	}

	public static <T, Z> LinkedHashMap<T, Z> reverseMap(Map<T, Z> map) {
		LinkedHashMap<T, Z> reversed = new LinkedHashMap<>();
		List<T> keys = new ArrayList<>(map.keySet());
		Collections.reverse(keys);
		for (T key : keys)
			reversed.put(key, map.get(key));
		return reversed;
	}

	public static String[] countdownToTime(int countdown) {
		String[] time = new String[] { String.valueOf(countdown / 60), String.valueOf(countdown % 60) };

		for (int i = 0; i < time.length; i++)
			if (time[i].length() == 1)
				time[i] = "0" + time[i];

		return time;
	}
}
