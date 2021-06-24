package de.cuuky.cfw.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
		String[] newStrings = new String[strings.size()];

		for (int i = 0; i < strings.size(); i++)
			newStrings[i] = strings.get(i);

		return newStrings;
	}

	public static ArrayList<String> collectionToArray(String[] strings) {
		ArrayList<String> newStrings = new ArrayList<>();
		for (String string : strings)
			newStrings.add(string);

		return newStrings;
	}

	public static String[] combineArrays(String[]... strings) {
		ArrayList<String> string = new ArrayList<>();

		for (String[] ss : strings)
			for (String strin : ss)
				string.add(strin);

		return getAsArray(string);
	}

	public static String getArgsToString(ArrayList<String> args, String insertBewteen) {
		String command = "";
		for (String arg : args)
			if (command.equals(""))
				command = arg;
			else
				command = command + insertBewteen + arg;

		return command;
	}

	public static String getArgsToString(String[] args, String insertBewteen) {
		String command = "";
		for (String arg : args)
			if (command.equals(""))
				command = arg;
			else
				command = command + insertBewteen + arg;

		return command;
	}

	public static String[] getAsArray(ArrayList<String> string) {
		String[] list = new String[string.size()];
		for (int i = 0; i < string.size(); i++)
			list[i] = string.get(i);

		return list;
	}

	public static ArrayList<Object> getAsList(String[] lis) {
		ArrayList<Object> list = new ArrayList<>();
		for (Object u : lis)
			list.add(u);

		return list;
	}

	public static int getNextToNine(int to) {
		int mod = to % 9;
		int result = (mod != 0 ? (9 - mod) : mod) + to;
		return Math.min(result, 54);
	}

	public static Object getStringObject(String obj) {
		try {
			return Integer.parseInt(obj);
		} catch (NumberFormatException e) {}

		try {
			return Long.parseLong(obj);
		} catch (NumberFormatException e) {}

		try {
			return Double.parseDouble(obj);
		} catch (NumberFormatException e) {}

		if (obj.equalsIgnoreCase("true") || obj.equalsIgnoreCase("false"))
			return obj.equalsIgnoreCase("true") ? true : false;
		else
			return obj;
	}

	/**
	 * @param min
	 *            The minimum Range
	 * @param max
	 *            The maximum Range
	 * @return Returns a random Integer between the min and the max range
	 */
	public static int randomInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
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
		String newMessage = "";
		boolean lastPara = false;
		for (char c : s.toCharArray()) {
			if (lastPara) {
				lastPara = false;
				continue;
			}

			if (c == '§' || c == '&') {
				lastPara = true;
				continue;
			}

			newMessage = newMessage.isEmpty() ? String.valueOf(c) : newMessage + c;
		}

		return newMessage;
	}

	public static String getCurrentDateAsFileable() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();

		return dateFormat.format(date);
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
		List<T> keys = new ArrayList<T>(map.keySet());
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