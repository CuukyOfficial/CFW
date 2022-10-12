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

package de.varoplugin.cfw.player.hud;

public class UnmodifiableAnimationData<T> implements AnimationData<T> {

    private final int delay;
    private final T[] frames;

    public UnmodifiableAnimationData(int delay, T[] frames) {
        if (delay < 0)
            throw new IllegalArgumentException("Delay < 0");

        if (frames == null || frames.length == 0)
            throw new IllegalArgumentException("Frames are null or empty");

        this.delay = delay;
        this.frames = frames;
    }

    @Override
    public int getDelay() {
        return this.delay;
    }

    @Override
    public int getNumFrames() {
        return this.frames.length;
    }

    @Override
    public T getFrame(int index) {
        return this.frames[index];
    }
}
