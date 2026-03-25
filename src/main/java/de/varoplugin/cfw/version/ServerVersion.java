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

package de.varoplugin.cfw.version;

import java.util.function.Supplier;

public enum ServerVersion {

    TWENTYSIX_1(26, 1, OneTwentyVersionAdapter::new),
    ONE_21(1, 21, OneTwentyVersionAdapter::new),
    ONE_20(1, 20, OneTwentyVersionAdapter::new),
    ONE_19(1, 19, OneNineteenVersionAdapter::new),
    ONE_18(1, 18, OneSeventeenVersionAdapter::new),
    ONE_17(1, 17, OneSeventeenVersionAdapter::new),
    ONE_16(1, 16, OneSixteenVersionAdapter::new),
    ONE_15(1, 15, OneFourteenVersionAdapter::new),
    ONE_14(1, 14, OneFourteenVersionAdapter::new),
    ONE_13(1, 13, OneThirteenVersionAdapter::new),
    ONE_12(1, 12, OneTwelveVersionAdapter::new),
    ONE_11(1, 11, OneNineVersionAdapter::new),
    ONE_10(1, 10, OneNineVersionAdapter::new),
    ONE_9(1, 9, OneNineVersionAdapter::new),
    ONE_8(1, 8, OneEightVersionAdapter::new),
    ONE_7(1, 7, OneSevenVersionAdapter::new),
    UNSUPPORTED(0, 0, UnsupportedVersionAdapter::new);

    private final int major;
    private final int minor;
    private final Supplier<VersionAdapter> adapterSupplier;

    ServerVersion(int major, int minor, Supplier<VersionAdapter> adapterSupplier) {
        this.major = major;
        this.minor = minor;
        this.adapterSupplier = adapterSupplier;
    }

    public boolean isHigherThan(ServerVersion ver) {
        return this.major > ver.major || (this.major == ver.major && this.minor > ver.minor);
    }

    public boolean isLowerThan(ServerVersion ver) {
        return this.major < ver.major || (this.major == ver.major && this.minor < ver.minor);
    }

    Supplier<VersionAdapter> getAdapterSupplier() {
        return this.adapterSupplier;
    }

    @Override
    public String toString() {
        return this.major == 0 && this.minor == 0 ? "UNSUPPORTED" : this.major + "." + this.minor;
    }

    public static ServerVersion getVersion(String versionString) {
        // Examples for possible version strings:
        // v1_8_R3
        // 26_1

        for (ServerVersion version : values())
            if (versionString.matches("v?" + version.major + "_" + version.minor + "(?:_.+)?"))
                return version;

        return values()[0]; // return latest and just hope it works :)
    }
}
