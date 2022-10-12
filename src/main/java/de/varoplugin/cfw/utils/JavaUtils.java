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

package de.varoplugin.cfw.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

public final class JavaUtils {

    private JavaUtils() {
    }

    public static String[] combineArrays(String[]... strings) {
        return Arrays.stream(strings).map(Arrays::asList).flatMap(List::stream).toArray(String[]::new);
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
        return IntStream.rangeClosed(0, string.length).filter(i -> i != loc).mapToObj(i -> string[i]).toArray(String[]::new);
    }

    public static boolean createFile(File file) {
        if (file.exists())
            return true;

        try {
            File parent = new File(file.getParent());
            return parent.mkdirs() & file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteDirectory(File file) {
        for (File listFile : Objects.requireNonNull(file.listFiles())) {
            if (listFile.isDirectory())
                deleteDirectory(listFile);
            listFile.delete();
        }

        return file.delete();
    }

    public static <T, Z> LinkedHashMap<T, Z> reverseMap(Map<T, Z> map) {
        LinkedHashMap<T, Z> reversed = new LinkedHashMap<>();
        List<T> keys = new ArrayList<>(map.keySet());
        Collections.reverse(keys);
        for (T key : keys)
            reversed.put(key, map.get(key));
        return reversed;
    }
}
