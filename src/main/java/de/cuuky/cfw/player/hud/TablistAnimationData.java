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

package de.cuuky.cfw.player.hud;

import java.util.Arrays;
import java.util.List;

public class TablistAnimationData extends AnimationData<String> {

	private static final String NEW_LINE = "\n";

	public TablistAnimationData(int delay, String[] frames) {
		super(delay, frames);
	}

	public TablistAnimationData(int delay, String[][] frames) {
		this(delay, Arrays.stream(frames).map(frame -> String.join(NEW_LINE, frame)).toArray(String[]::new));
	}

	public TablistAnimationData(int delay, List<List<String>> frames) {
		this(delay, frames.stream().map(frame -> String.join(NEW_LINE, frame)).toArray(String[]::new));
	}
}
