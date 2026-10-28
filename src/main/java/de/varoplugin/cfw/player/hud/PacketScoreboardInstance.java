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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import de.varoplugin.cfw.version.VersionUtils;

/**
 * {@link ScoreboardInstance} that does not use the Bukkit scoreboard API but sends the scoreboard packets directly to the player. This is
 * required on Folia, where scoreboards cannot be created or assigned to players. All state is kept by this class, the actual packets are
 * created by a {@link PacketFactory} which is specific to the server version. Sending packets is thread safe.
 */
class PacketScoreboardInstance implements ScoreboardInstance {

    private static final String OBJECTIVE_NAME = "CFW";

    /**
     * @return Whether scoreboards have to be sent as packets on this server. This is the case on Folia. Packet scoreboards can be forced by
     *         setting the system property {@code cfw.packetScoreboards}.
     */
    static boolean isRequired() {
        return VersionUtils.getServerSoftware().isRegionized() || Boolean.getBoolean("cfw.packetScoreboards");
    }

    static boolean isSupported() {
        return Holder.FACTORY != null;
    }

    /**
     * Lazily initialized, as the factories must only be loaded if packet scoreboards are actually used
     */
    private static class Holder {

        private static final PacketFactory FACTORY = createFactory();

        private static PacketFactory createFactory() {
            try {
                return PacketFactory.create();
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
                Bukkit.getLogger().log(Level.SEVERE, "[CFW] Packet based scoreboards are not supported on this server version, scoreboards are disabled: " + e, e);
                return null;
            }
        }
    }

    private static class Team {

        private final Set<String> members = new HashSet<>();
        private boolean visible = true;
        private String prefix = "", suffix = "";
    }

    private final Player player;
    private final PacketFactory factory = Holder.FACTORY;
    private final Map<String, Team> teams = new HashMap<>();
    private final Map<String, String> entryTeams = new HashMap<>();
    private boolean sideBar;
    private String title = "";

    PacketScoreboardInstance(Player player) {
        this.player = player;
    }

    private void send(Object packet) {
        this.factory.send(this.player, packet);
    }

    @Override
    public synchronized Objective registerSideBar() {
        if (this.sideBar)
            throw new IllegalStateException("The sidebar is already registered");

        this.sideBar = true;
        this.send(this.factory.objective(OBJECTIVE_NAME, this.title, PacketFactory.OBJECTIVE_ADD));
        this.send(this.factory.sideBar(OBJECTIVE_NAME));
        return null;
    }

    @Override
    public Objective getSideBar() {
        throw new UnsupportedOperationException("Not backed by the Bukkit scoreboard API");
    }

    @Override
    public synchronized void clearSideBar() {
        if (!this.sideBar)
            return;

        this.sideBar = false;
        this.send(this.factory.objective(OBJECTIVE_NAME, this.title, PacketFactory.OBJECTIVE_REMOVE));
    }

    @Override
    public synchronized void setSideBarTitle(String title) {
        this.title = title;
        if (this.sideBar)
            this.send(this.factory.objective(OBJECTIVE_NAME, title, PacketFactory.OBJECTIVE_UPDATE));
    }

    @Override
    public synchronized void setSideBarScore(String line, int score) {
        if (this.sideBar)
            this.send(this.factory.score(line, OBJECTIVE_NAME, score));
    }

    @Override
    public synchronized void resetSideBarScore(String line) {
        if (this.sideBar)
            this.send(this.factory.resetScore(line, OBJECTIVE_NAME));
    }

    @Override
    public synchronized void setNameTag(Player target, boolean visible, String teamName, String prefix, String suffix) {
        String entry = target.getName();
        String currentTeam = this.entryTeams.get(entry);

        if (currentTeam != null && !currentTeam.equals(teamName)) {
            this.removeTeam(currentTeam);
            currentTeam = null;
        }

        Team team = this.teams.get(teamName);
        boolean created = team == null;
        if (created) {
            team = new Team();
            this.teams.put(teamName, team);
        }

        prefix = prefix == null ? "" : prefix;
        suffix = suffix == null ? "" : suffix;
        boolean changed = team.visible != visible || !team.prefix.equals(prefix) || !team.suffix.equals(suffix);
        team.visible = visible;
        team.prefix = prefix;
        team.suffix = suffix;

        if (created)
            this.send(this.factory.team(teamName, PacketFactory.TEAM_CREATE, visible, prefix, suffix, team.members));
        else if (changed)
            this.send(this.factory.team(teamName, PacketFactory.TEAM_UPDATE, visible, prefix, suffix, team.members));

        if (currentTeam == null) {
            team.members.add(entry);
            this.entryTeams.put(entry, teamName);
            this.send(this.factory.teamMember(teamName, entry, true));
        }
    }

    private void removeTeam(String teamName) {
        if (this.teams.remove(teamName) == null)
            return;

        this.entryTeams.values().removeIf(teamName::equals);
        this.send(this.factory.teamRemove(teamName));
    }

    @Override
    public synchronized void destroy() {
        this.clearSideBar();
        for (String teamName : new HashSet<>(this.teams.keySet()))
            this.removeTeam(teamName);
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public Scoreboard getScoreboard() {
        throw new UnsupportedOperationException("Not backed by the Bukkit scoreboard API");
    }

    /**
     * Creates the scoreboard packets for one specific range of server versions. Implementations are stateless.
     */
    interface PacketFactory {

        int OBJECTIVE_ADD = 0, OBJECTIVE_REMOVE = 1, OBJECTIVE_UPDATE = 2;
        int TEAM_CREATE = 0, TEAM_UPDATE = 2;

        Object objective(String name, String title, int mode);

        /**
         * Displays the objective in the sidebar slot
         */
        Object sideBar(String objective);

        Object score(String owner, String objective, int score);

        Object resetScore(String owner, String objective);

        Object team(String name, int mode, boolean nameTagVisible, String prefix, String suffix, Collection<String> members);

        Object teamMember(String name, String entry, boolean add);

        Object teamRemove(String name);

        void send(Player player, Object packet);

        /**
         * @return The factory for the current server version
         * @throws ReflectiveOperationException If the server version is not supported
         */
        static PacketFactory create() throws ReflectiveOperationException {
            try {
                // Servers using the Mojang mappings at runtime (Paper 1.20.5+)
                Class.forName("net.minecraft.network.chat.Component");
                return new MojangPacketFactory();
            } catch (ClassNotFoundException e) {
                // Spigot mappings (Folia 1.19.4 - 1.20.4)
                return new SpigotPacketFactory();
            }
        }
    }
}
