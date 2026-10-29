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

import org.bukkit.entity.Player;

/**
 * A per player scoreboard that offers a sidebar and name tags (teams).
 * <p>
 * On servers that do not support the Bukkit scoreboard API (Folia) the scoreboard is sent to the player using packets. Everything that is
 * available on every platform is part of this interface, instances backed by the Bukkit API additionally implement {@link BukkitScoreboardInstance}.
 */
public interface ScoreboardInstance {

    void registerSideBar();

    void clearSideBar();

    /**
     * Sets the title of the sidebar. The sidebar has to be registered.
     */
    void setSideBarTitle(String title);

    /**
     * Sets the score of a line of the sidebar. The sidebar has to be registered.
     */
    void setSideBarScore(String line, int score);

    /**
     * Removes a line from the sidebar. The sidebar has to be registered.
     */
    void resetSideBarScore(String line);

    /**
     * Puts a player into the team of the given name on this scoreboard, creating the team if required, and updates the
     * team's display values.
     */
    void setNameTag(Player target, boolean visible, String teamName, String prefix, String suffix);

    /**
     * Removes everything this instance displayed to the player. The instance must not be used afterwards.
     */
    void destroy();

    Player getPlayer();

    /**
     * @return Whether scoreboards can be created on this server. This is always the case if the Bukkit scoreboard API is available. On Folia
     *         scoreboards are sent using packets which requires a Mojang mapped server (1.20.5+).
     */
    static boolean isSupported() {
        return !PacketScoreboardInstance.isRequired() || PacketScoreboardInstance.isSupported();
    }

    /**
     * @return Whether scoreboards are sent as packets instead of using the Bukkit scoreboard API (Folia). The Bukkit scoreboard of players must
     *         not be touched in that case.
     */
    static boolean isPacketBased() {
        return PacketScoreboardInstance.isRequired();
    }

    /**
     * @return A new scoreboard instance for the player
     * @throws UnsupportedOperationException If {@link #isSupported()} is {@code false}
     */
    static ScoreboardInstance newInstance(Player player) {
        if (!isSupported())
            throw new UnsupportedOperationException("Scoreboards are not supported on this server");

        return PacketScoreboardInstance.isRequired() ? new PacketScoreboardInstance(player) : new DefaultScoreboardInstance(player);
    }
}
