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

package de.varoplugin.cfw.configuration.serialization;

import java.util.function.Function;

public class SerializationPolicy<T, C> {

    private final Function<C, T> getter;
    private final Function<T, C> receiver;

    SerializationPolicy(Function<C, T> getter, Function<T, C> receiver) {
        this.getter = getter;
        this.receiver = receiver;
    }

    @SuppressWarnings("unchecked")
    public T serialize(Object from) {
        if (this.getter == null)
            return null;

        return this.getter.apply((C) from);
    }

    @SuppressWarnings("unchecked")
    public C deserialize(Object type) {
        if (this.receiver == null)
            return null;

        return this.receiver.apply((T) type);
    }
}
