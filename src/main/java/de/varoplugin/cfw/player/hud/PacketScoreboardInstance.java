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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import de.varoplugin.cfw.version.VersionUtils;

/**
 * {@link ScoreboardInstance} that does not use the Bukkit scoreboard API but sends the scoreboard packets directly to the player. This is
 * required on Folia, where scoreboards cannot be created or assigned to players. The vanilla scoreboard classes are only used to build the
 * packets, they are never registered anywhere. Sending packets is thread safe.
 * <p>
 * Uses the Mojang mappings and is therefore limited to servers that are not remapped (Paper 1.20.5+).
 */
class PacketScoreboardInstance implements ScoreboardInstance {

    private static final String OBJECTIVE_NAME = "CFW";
    private static final int MODE_ADD = 0, MODE_REMOVE = 1, MODE_UPDATE = 2;


    /**
     * @return Whether scoreboards have to be sent as packets on this server. This is the case on Folia. Packet scoreboards can be forced by
     *         setting the system property {@code cfw.packetScoreboards}.
     */
    static boolean isRequired() {
        return VersionUtils.getServerSoftware().isRegionized() || Boolean.getBoolean("cfw.packetScoreboards");
    }

    static boolean isSupported() {
        return Holder.ACCESSOR != null;
    }

    /**
     * Lazily initialized, as it must only be loaded if packet scoreboards are actually used
     */
    private static class Holder {

        private static final Accessor ACCESSOR = Accessor.create();
    }

    private final Player player;
    private final Object scoreboard;
    private final Map<String, Object> teams = new HashMap<>();
    private final Map<String, String> entryTeams = new HashMap<>();
    private Object objective;
    private String title = "";

    PacketScoreboardInstance(Player player) {
        this.player = player;
        this.scoreboard = Holder.ACCESSOR.newScoreboard();
    }

    @Override
    public synchronized Objective registerSideBar() {
        if (this.objective != null)
            throw new IllegalStateException("The sidebar is already registered");

        this.objective = Holder.ACCESSOR.newObjective(this.scoreboard, OBJECTIVE_NAME, this.title);
        this.send(Holder.ACCESSOR.objectivePacket(this.objective, MODE_ADD));
        this.send(Holder.ACCESSOR.displayPacket(this.objective));
        return null;
    }

    @Override
    public Objective getSideBar() {
        throw new UnsupportedOperationException("Not backed by the Bukkit scoreboard API");
    }

    @Override
    public synchronized void clearSideBar() {
        if (this.objective == null)
            return;

        this.send(Holder.ACCESSOR.objectivePacket(this.objective, MODE_REMOVE));
        Holder.ACCESSOR.removeObjective(this.scoreboard, this.objective);
        this.objective = null;
    }

    @Override
    public synchronized void setSideBarTitle(String title) {
        this.title = title;
        if (this.objective == null)
            return;

        Holder.ACCESSOR.setObjectiveTitle(this.objective, title);
        this.send(Holder.ACCESSOR.objectivePacket(this.objective, MODE_UPDATE));
    }

    @Override
    public synchronized void setSideBarScore(String line, int score) {
        if (this.objective != null)
            this.send(Holder.ACCESSOR.scorePacket(line, OBJECTIVE_NAME, score));
    }

    @Override
    public synchronized void resetSideBarScore(String line) {
        if (this.objective != null)
            this.send(Holder.ACCESSOR.resetScorePacket(line, OBJECTIVE_NAME));
    }

    @Override
    public synchronized void setNameTag(Player target, boolean visible, String teamName, String prefix, String suffix) {
        String entry = target.getName();
        String currentTeam = this.entryTeams.get(entry);

        if (currentTeam != null && !currentTeam.equals(teamName)) {
            this.removeTeam(currentTeam);
            currentTeam = null;
        }

        Object team = this.teams.get(teamName);
        boolean created = team == null;
        if (created) {
            team = Holder.ACCESSOR.newTeam(this.scoreboard, teamName);
            this.teams.put(teamName, team);
        }

        boolean changed = Holder.ACCESSOR.setTeamValues(team, visible, prefix, suffix);
        if (created) {
            this.send(Holder.ACCESSOR.teamPacket(team, true));
        } else if (changed)
            this.send(Holder.ACCESSOR.teamPacket(team, false));

        if (currentTeam == null) {
            Holder.ACCESSOR.teamMembers(team).add(entry);
            this.entryTeams.put(entry, teamName);
            this.send(Holder.ACCESSOR.teamMemberPacket(team, entry, true));
        }
    }

    private void removeTeam(String teamName) {
        Object team = this.teams.remove(teamName);
        if (team == null)
            return;

        this.entryTeams.values().removeIf(teamName::equals);
        this.send(Holder.ACCESSOR.teamRemovePacket(team));
    }

    @Override
    public synchronized void destroy() {
        this.clearSideBar();
        for (String teamName : new HashSet<>(this.teams.keySet()))
            this.removeTeam(teamName);
    }

    private void send(Object packet) {
        Holder.ACCESSOR.send(this.player, packet);
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
     * Reflective access to the vanilla scoreboard classes. The relevant signatures are identical in all Mojang mapped versions since 1.20.5.
     */
    private static class Accessor {

        private static final String NMS = "net.minecraft.";

        private final Method fromString;
        private final Method getHandle;
        private final Field connection;
        private final Method send;
        private final Constructor<?> scoreboard;
        private final Method addObjective, removeObjective;
        private final Object criteriaDummy;
        private final Object renderTypeInteger;
        private final Method setObjectiveTitle;
        private final Constructor<?> objectivePacket;
        private final Constructor<?> displayPacket;
        private final Object sidebarSlot;
        private final Constructor<?> scorePacket;
        private final Constructor<?> resetScorePacket;
        private final Constructor<?> team;
        private final Method setPrefix, setSuffix, setNameTagVisibility, getPrefix, getSuffix, getNameTagVisibility, getPlayers;
        private final Object visibilityAlways, visibilityNever;
        private final Method teamAddOrModify, teamRemove, teamPlayer;
        private final Object actionAdd, actionRemove;

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private Accessor() throws ReflectiveOperationException {
            Class<?> craftServer = Bukkit.getServer().getClass();
            String craftPackage = craftServer.getPackage().getName();
            Class<?> component = Class.forName(NMS + "network.chat.Component");
            Class<?> packet = Class.forName(NMS + "network.protocol.Packet");

            this.fromString = Class.forName(craftPackage + ".util.CraftChatMessage").getMethod("fromStringOrEmpty", String.class);
            Class<?> craftPlayer = Class.forName(craftPackage + ".entity.CraftPlayer");
            this.getHandle = craftPlayer.getMethod("getHandle");
            Class<?> serverPlayer = this.getHandle.getReturnType();
            this.connection = serverPlayer.getField("connection");
            this.send = this.connection.getType().getMethod("send", packet);

            Class<?> scoreboardClass = Class.forName(NMS + "world.scores.Scoreboard");
            Class<?> objectiveClass = Class.forName(NMS + "world.scores.Objective");
            Class<?> criteria = Class.forName(NMS + "world.scores.criteria.ObjectiveCriteria");
            Class<?> renderType = Class.forName(NMS + "world.scores.criteria.ObjectiveCriteria$RenderType");
            Class<?> numberFormat = Class.forName(NMS + "network.chat.numbers.NumberFormat");
            this.scoreboard = scoreboardClass.getConstructor();
            this.addObjective = scoreboardClass.getMethod("addObjective", String.class, criteria, component, renderType, boolean.class, numberFormat);
            this.removeObjective = scoreboardClass.getMethod("removeObjective", objectiveClass);
            this.criteriaDummy = criteria.getField("DUMMY").get(null);
            this.renderTypeInteger = renderType.getField("INTEGER").get(null);
            this.setObjectiveTitle = objectiveClass.getMethod("setDisplayName", component);

            Class<?> displaySlot = Class.forName(NMS + "world.scores.DisplaySlot");
            this.objectivePacket = Class.forName(NMS + "network.protocol.game.ClientboundSetObjectivePacket").getConstructor(objectiveClass, int.class);
            this.displayPacket = Class.forName(NMS + "network.protocol.game.ClientboundSetDisplayObjectivePacket").getConstructor(displaySlot, objectiveClass);
            this.sidebarSlot = Enum.valueOf((Class<Enum>) displaySlot, "SIDEBAR");
            this.scorePacket = Class.forName(NMS + "network.protocol.game.ClientboundSetScorePacket").getConstructor(String.class, String.class, int.class, Optional.class, Optional.class);
            this.resetScorePacket = Class.forName(NMS + "network.protocol.game.ClientboundResetScorePacket").getConstructor(String.class, String.class);

            Class<?> teamClass = Class.forName(NMS + "world.scores.PlayerTeam");
            Class<?> visibility = Class.forName(NMS + "world.scores.Team$Visibility");
            this.team = teamClass.getConstructor(scoreboardClass, String.class);
            this.setPrefix = teamClass.getMethod("setPlayerPrefix", component);
            this.setSuffix = teamClass.getMethod("setPlayerSuffix", component);
            this.getPrefix = teamClass.getMethod("getPlayerPrefix");
            this.getSuffix = teamClass.getMethod("getPlayerSuffix");
            this.setNameTagVisibility = teamClass.getMethod("setNameTagVisibility", visibility);
            this.getNameTagVisibility = teamClass.getMethod("getNameTagVisibility");
            this.getPlayers = teamClass.getMethod("getPlayers");
            this.visibilityAlways = Enum.valueOf((Class<Enum>) visibility, "ALWAYS");
            this.visibilityNever = Enum.valueOf((Class<Enum>) visibility, "NEVER");

            Class<?> teamPacket = Class.forName(NMS + "network.protocol.game.ClientboundSetPlayerTeamPacket");
            Class<?> action = Class.forName(NMS + "network.protocol.game.ClientboundSetPlayerTeamPacket$Action");
            this.teamAddOrModify = teamPacket.getMethod("createAddOrModifyPacket", teamClass, boolean.class);
            this.teamRemove = teamPacket.getMethod("createRemovePacket", teamClass);
            this.teamPlayer = teamPacket.getMethod("createPlayerPacket", teamClass, String.class, action);
            this.actionAdd = Enum.valueOf((Class<Enum>) action, "ADD");
            this.actionRemove = Enum.valueOf((Class<Enum>) action, "REMOVE");
        }

        static Accessor create() {
            try {
                return new Accessor();
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
                Bukkit.getLogger().log(Level.SEVERE, "[CFW] Packet based scoreboards are not supported on this server version, scoreboards are disabled: " + e, e);
                return null;
            }
        }

        private static RuntimeException rethrow(ReflectiveOperationException e) {
            Throwable cause = e instanceof InvocationTargetException && e.getCause() != null ? e.getCause() : e;
            return cause instanceof RuntimeException ? (RuntimeException) cause : new IllegalStateException(cause);
        }

        private Object component(String text) {
            try {
                return this.fromString.invoke(null, text);
            } catch (ReflectiveOperationException e) {
                throw rethrow(e);
            }
        }

        private Object create(Constructor<?> constructor, Object... args) {
            try {
                return constructor.newInstance(args);
            } catch (ReflectiveOperationException e) {
                throw rethrow(e);
            }
        }

        private Object invoke(Method method, Object target, Object... args) {
            try {
                return method.invoke(target, args);
            } catch (ReflectiveOperationException e) {
                throw rethrow(e);
            }
        }

        Object newScoreboard() {
            return this.create(this.scoreboard);
        }

        Object newObjective(Object scoreboard, String name, String title) {
            return this.invoke(this.addObjective, scoreboard, name, this.criteriaDummy, this.component(title), this.renderTypeInteger, false, null);
        }

        void removeObjective(Object scoreboard, Object objective) {
            this.invoke(this.removeObjective, scoreboard, objective);
        }

        void setObjectiveTitle(Object objective, String title) {
            this.invoke(this.setObjectiveTitle, objective, this.component(title));
        }

        Object objectivePacket(Object objective, int mode) {
            return this.create(this.objectivePacket, objective, mode);
        }

        Object displayPacket(Object objective) {
            return this.create(this.displayPacket, this.sidebarSlot, objective);
        }

        Object scorePacket(String owner, String objective, int score) {
            return this.create(this.scorePacket, owner, objective, score, Optional.empty(), Optional.empty());
        }

        Object resetScorePacket(String owner, String objective) {
            return this.create(this.resetScorePacket, owner, objective);
        }

        Object newTeam(Object scoreboard, String name) {
            return this.create(this.team, scoreboard, name);
        }

        /**
         * @return Whether any value changed
         */
        boolean setTeamValues(Object team, boolean visible, String prefix, String suffix) {
            Object newPrefix = this.component(prefix == null ? "" : prefix);
            Object newSuffix = this.component(suffix == null ? "" : suffix);
            Object newVisibility = visible ? this.visibilityAlways : this.visibilityNever;

            boolean changed = !newPrefix.equals(this.invoke(this.getPrefix, team)) || !newSuffix.equals(this.invoke(this.getSuffix, team))
                    || newVisibility != this.invoke(this.getNameTagVisibility, team);
            if (changed) {
                this.invoke(this.setPrefix, team, newPrefix);
                this.invoke(this.setSuffix, team, newSuffix);
                this.invoke(this.setNameTagVisibility, team, newVisibility);
            }
            return changed;
        }

        @SuppressWarnings("unchecked")
        java.util.Collection<String> teamMembers(Object team) {
            return (java.util.Collection<String>) this.invoke(this.getPlayers, team);
        }

        Object teamPacket(Object team, boolean add) {
            return this.invoke(this.teamAddOrModify, null, team, add);
        }

        Object teamRemovePacket(Object team) {
            return this.invoke(this.teamRemove, null, team);
        }

        Object teamMemberPacket(Object team, String entry, boolean add) {
            return this.invoke(this.teamPlayer, null, team, entry, add ? this.actionAdd : this.actionRemove);
        }

        void send(Player player, Object packet) {
            try {
                Object connection = this.connection.get(this.getHandle.invoke(player));
                // The connection is not available yet while the player is still logging in
                if (connection != null)
                    this.send.invoke(connection, packet);
            } catch (ReflectiveOperationException e) {
                throw rethrow(e);
            }
        }
    }
}
