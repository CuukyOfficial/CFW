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

import java.util.Arrays;

@Deprecated
public enum ProtocolVersion {

    // Protocol version numbers: https://wiki.vg/Protocol_version_numbers

    MINECRAFT_1_20_1(764, "1.20.1"),
    MINECRAFT_1_20(763, "1.20.0"),
    MINECRAFT_1_19_4(762, "1.19.4"),
    MINECRAFT_1_19_3(761, "1.19.3"),
    MINECRAFT_1_19_2(760, "1.19.1-1.19.2"),
    MINECRAFT_1_19(759, "1.19"),
    MINECRAFT_1_18_2(758, "1.18.2"),
    MINECRAFT_1_18_1(757, "1.18-1.18.1"),
    MINECRAFT_1_17_1(756, "1.17.1"),
    MINECRAFT_1_17(755, "1.17"),
    MINECRAFT_1_16_5(754, "1.16.4-1.16.5"),
    MINECRAFT_1_16_3(753, "1.16.3"),
    MINECRAFT_1_16_2(751, "1.16.2"),
    MINECRAFT_1_16_1(736, "1.16.1"),
    MINECRAFT_1_16(735, "1.16"),
    MINECRAFT_1_15_2(578, "1.15.2"),
    MINECRAFT_1_15_1(575, "1.15.1"),
    MINECRAFT_1_15(573, "1.15"),
    MINECRAFT_1_14_4(498, "1.14.4"),
    MINECRAFT_1_14_3(490, "1.14.3"),
    MINECRAFT_1_14_2(485, "1.14.2"),
    MINECRAFT_1_14_1(480, "1.14.1"),
    MINECRAFT_1_14(477, "1.14"),
    MINECRAFT_1_13_2(404, "1.13.2"),
    MINECRAFT_1_13_1(401, "1.13.1"),
    MINECRAFT_1_13(393, "1.13"),
    MINECRAFT_1_12_2(340, "1.12.2"),
    MINECRAFT_1_12_1(338, "1.12.1"),
    MINECRAFT_1_12(335, "1.12"),
    MINECRAFT_1_11_1(316, "1.11.2"),
    MINECRAFT_1_11(315, "1.11"),
    MINECRAFT_1_10(210, "1.10"),
    MINECRAFT_1_9_4(110, "1.9.3-1.9.4"),
    MINECRAFT_1_9_2(109, "1.9.2"),
    MINECRAFT_1_9_1(108, "1.9.1"),
    MINECRAFT_1_9(107, "1.9"),
    MINECRAFT_1_8(47, "1.8-1.8.9"),
    MINECRAFT_1_7_10(5, "1.7.6-1.7.10"),
    MINECRAFT_1_7_2(4, "1.7.2-1.7.5"),
    UNKNWON(-1, "Unknown");

    private final int protocolId;
    private final String name;

    ProtocolVersion(int id, String name) {
        this.name = name;
        this.protocolId = id;
    }

    public boolean isHigherThan(ProtocolVersion version) {
        return this.protocolId > version.getProtocolId();
    }

    public boolean isLowerThan(ProtocolVersion version) {
        return this.protocolId < version.getProtocolId();
    }

    public String getName() {
        return this.name;
    }

    public int getProtocolId() {
        return this.protocolId;
    }

    public static ProtocolVersion getMinecraftVersion(int protocolId) {
        ProtocolVersion version = MINECRAFT_1_7_2;
        for (ProtocolVersion mcver : ProtocolVersion.values())
            if (protocolId >= mcver.getProtocolId() && mcver.isHigherThan(version))
                version = mcver;

        return version;
    }

    public static ProtocolVersion getMinecraftVersion(String versionname) {
        return Arrays.stream(ProtocolVersion.values()).filter(ver -> ver.getName().equals(versionname)).findFirst().orElse(null);
    }
}
