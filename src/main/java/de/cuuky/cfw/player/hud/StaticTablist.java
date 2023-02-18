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

import org.bukkit.entity.Player;

import de.cuuky.cfw.player.CustomPlayer;
import de.cuuky.cfw.version.VersionAdapter;
import de.cuuky.cfw.version.VersionUtils;

public class StaticTablist {

    private static final VersionAdapter VERISON_ADAPTER = VersionUtils.getVersionAdapter();

    private final CustomPlayer player;
    private String headerBuffer;
    private String footerBuffer;
    private boolean headerEnabled;
    private boolean footerEnabled;

    public StaticTablist(CustomPlayer player, String header, String footer) {
        this.player = player;
        this.headerEnabled = true;
        this.footerEnabled = true;

        this.update(header, footer);
    }

    public StaticTablist(CustomPlayer player) {
        this.player = player;
        this.headerEnabled = false;
        this.footerEnabled = false;
        this.headerBuffer = "";
        this.footerBuffer = "";

        this.update(null, null);
    }

    /**
     * Updates the tablist
     * 
     * @param header The header, may be null
     * @param footer The footer, may be null
     */
    public void update(String header, String footer) {
        this.headerBuffer = this.headerEnabled ? header == null ? this.headerBuffer : this.processString(header) : "";
        this.footerBuffer = this.footerEnabled ? footer == null ? this.footerBuffer : this.processString(footer) : "";

        Player player = this.player.getPlayer();

        if (player != null)
            VERISON_ADAPTER.sendTablist(player, this.headerBuffer, this.footerBuffer);
    }

    protected String processString(String input) {
        return input;
    }

    /**
     * Enables or disables the header. {@link StaticTablist#update} has to be invoked for the change to take affect
     * 
     * @param headerEnabled True if enabled
     */
    public void setHeaderEnabled(boolean headerEnabled) {
        this.headerEnabled = headerEnabled;
    }

    public boolean isHeaderEnabled() {
        return this.headerEnabled;
    }

    /**
     * Enables or disables the footer. {@link StaticTablist#update} has to be invoked for the change to take affect
     * 
     * @param footerEnabled True if enabled
     */
    public void setFooterEnabled(boolean footerEnabled) {
        this.footerEnabled = footerEnabled;
    }

    public boolean isFooterEnabled() {
        return this.footerEnabled;
    }
}
