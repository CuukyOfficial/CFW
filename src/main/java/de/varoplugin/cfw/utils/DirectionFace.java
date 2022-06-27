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

public enum DirectionFace {

    NORTH("NORTH", 135f, -135f),
    EAST("EAST", -135f, -45f),
    SOUTH("SOUTH", -45f, 45f),
    WEST("WEST", 45f, 135f);

    private final String identifier;
    private final float start;
    private final float end;

    DirectionFace(String identifier, float start, float end) {
        this.identifier = identifier;
        this.start = start;
        this.end = end;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isIn(float yaw) {
        return start <= yaw && end > yaw;
    }

    public double[] modifyValues(double x, double z) {
        switch (this) {
            case EAST:
                return new double[] { -z, x };
            case WEST:
                return new double[] { z, -x };
            case SOUTH:
                return new double[] { -x, -z };
            default:
                break;
        }

        return new double[] { x, z };
    }

    public static DirectionFace getFace(float yaw) {
        yaw = yaw >= 180 ? -180 + (yaw - 180) : yaw;
        for (DirectionFace face : values())
            if (face.isIn(yaw))
                return face;

        return NORTH;
    }
}
