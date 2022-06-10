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

import java.util.function.Function;
import java.util.function.Supplier;

public enum ServerSoftware {

    MAGMA("Magma", versionSupplier -> new MagmaVersionAdapter(), "org.magmafoundation.magma.Magma", "Magma"),
    CRUCIBLE("Crucible", versionSupplier -> new CrucibleVersionAdapter(), "io.github.crucible.Crucible", "Crucible"),
    URANIUM("Uranium", null, null, "Uranium"),
    THERMOS("Thermos", null, "thermos.Thermos", "Thermos"),
    CAULDRON("Cauldron", null, null, "Cauldron"),
    SPORT_PAPER("SportPaper", SpigotVersionAdapter::new, "org.github.paperspigot.SharedConfig", "SportPaper"),
    NACHO("NachoSpigot", SpigotVersionAdapter::new, "me.elier.nachospigot.config.NachoConfig", "Nacho", "NachoSpigot"),
    TACO("TacoSpigot", SpigotVersionAdapter::new, "net.techcable.tacospigot.TacoSpigotConfig", "Taco", "TacoSpigot"),
    PAPER("Paper", SpigotVersionAdapter::new, "co.aikar.timings.Timings", "Paper", "PaperSpigot"),
    SPIGOT("Spigot", SpigotVersionAdapter::new, "org.spigotmc.SpigotConfig", "Spigot"),
    BUKKIT("CraftBukkit", null, "org.bukkit.Bukkit", "CraftBukkit", "Bukkit"),
    UNKNOWN("Unknown", null, null);

    private final String name;
    private final String[] versionnames;
    private String identifierClass;
    private final Function<Supplier<VersionAdapter>, VersionAdapter> adapterFunction;
    private VersionAdapter adapter;

    private static ServerSoftware currentSoftware = null;

    /**
     * @param name            Display name for platform.
     * @param adapterFunction Version adapter for this platform
     * @param identifierClass Class that identifies this platform
     * @param versionnames    Names the platform could be known as
     */
    ServerSoftware(String name, Function<Supplier<VersionAdapter>, VersionAdapter> adapterFunction, String identifierClass, String... versionnames) {
        this.name = name;
        this.versionnames = versionnames;
        this.identifierClass = identifierClass == null ? "" : identifierClass;
        this.adapterFunction = adapterFunction == null ? Supplier::get : adapterFunction;
    }

    /**
     * @return Name of the software
     **/
    public String getName() {
        return this.name;
    }

    /**
     * @return Names of the software
     **/
    public String[] getVersionNames() {
        return this.versionnames;
    }

    /**
     * @return Name of the identifier class
     **/
    public String getIdentifierClass() {
        return this.identifierClass;
    }

    VersionAdapter getVersionAdapter(Supplier<VersionAdapter> bukkitVersionSupplier) {
        if (this.adapter == null)
            return this.adapter = this.adapterFunction.apply(bukkitVersionSupplier);
        else
            return this.adapter;
    }

    /**
     * @return Software the server is running on or the next highest one on the fork chain
     **/
    static ServerSoftware getServerSoftware() {

        // Don't check every time, it's not going to change
        if (currentSoftware == null) {
            // Order is important due to fork chain
            for (ServerSoftware software : values()) {
                if (VersionUtils.isClassPresent(software.getIdentifierClass())) {
                    currentSoftware = software;
                    break;
                }
            }
        }
        return currentSoftware;
    }

}
