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
import java.util.Collection;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.varoplugin.cfw.player.hud.PacketScoreboardInstance.PacketFactory;

/**
 * Builds packets using the vanilla scoreboard classes (Mojang mappings, Paper 1.20.5+). The scoreboard objects are only used to create the
 * packets, they are never registered anywhere.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
class MojangPacketFactory implements PacketFactory {

    private static final String NMS = "net.minecraft.";

    private final Method fromString;
    private final Method getHandle;
    private final Field connection;
    private final Method send;
    private final Constructor<?> scoreboard;
    private final Method addObjective;
    private final Object criteriaDummy;
    private final Object renderTypeInteger;
    private final Constructor<?> objectivePacket;
    private final Constructor<?> displayPacket;
    private final Object sidebarSlot;
    private final Constructor<?> scorePacket;
    private final Constructor<?> resetScorePacket;
    private final Constructor<?> team;
    private final Method setPrefix, setSuffix, setNameTagVisibility, getPlayers;
    private final Object visibilityAlways, visibilityNever;
    private final Method teamAddOrModify, teamRemove, teamPlayer;
    private final Object actionAdd, actionRemove;

    MojangPacketFactory() throws ReflectiveOperationException {
        String craftPackage = Bukkit.getServer().getClass().getPackage().getName();
        Class<?> component = Class.forName(NMS + "network.chat.Component");
        Class<?> packet = Class.forName(NMS + "network.protocol.Packet");

        this.fromString = Class.forName(craftPackage + ".util.CraftChatMessage").getMethod("fromStringOrEmpty", String.class);
        this.getHandle = Class.forName(craftPackage + ".entity.CraftPlayer").getMethod("getHandle");
        this.connection = this.getHandle.getReturnType().getField("connection");
        this.send = this.connection.getType().getMethod("send", packet);

        Class<?> scoreboardClass = Class.forName(NMS + "world.scores.Scoreboard");
        Class<?> objectiveClass = Class.forName(NMS + "world.scores.Objective");
        Class<?> criteria = Class.forName(NMS + "world.scores.criteria.ObjectiveCriteria");
        Class<?> renderType = Class.forName(NMS + "world.scores.criteria.ObjectiveCriteria$RenderType");
        Class<?> numberFormat = Class.forName(NMS + "network.chat.numbers.NumberFormat");
        this.scoreboard = scoreboardClass.getConstructor();
        this.addObjective = scoreboardClass.getMethod("addObjective", String.class, criteria, component, renderType, boolean.class, numberFormat);
        this.criteriaDummy = criteria.getField("DUMMY").get(null);
        this.renderTypeInteger = renderType.getField("INTEGER").get(null);

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
        this.setNameTagVisibility = teamClass.getMethod("setNameTagVisibility", visibility);
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

    private Object component(String text) throws ReflectiveOperationException {
        return this.fromString.invoke(null, text);
    }

    private Object newObjective(String name, String title) throws ReflectiveOperationException {
        return this.addObjective.invoke(this.scoreboard.newInstance(), name, this.criteriaDummy, this.component(title), this.renderTypeInteger, false, null);
    }

    private Object newTeam(String name) throws ReflectiveOperationException {
        return this.team.newInstance(this.scoreboard.newInstance(), name);
    }

    @Override
    public Object objective(String name, String title, int mode) {
        try {
            return this.objectivePacket.newInstance(this.newObjective(name, title), mode);
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }

    @Override
    public Object sideBar(String objective) {
        try {
            return this.displayPacket.newInstance(this.sidebarSlot, this.newObjective(objective, ""));
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }

    @Override
    public Object score(String owner, String objective, int score) {
        try {
            return this.scorePacket.newInstance(owner, objective, score, Optional.empty(), Optional.empty());
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }

    @Override
    public Object resetScore(String owner, String objective) {
        try {
            return this.resetScorePacket.newInstance(owner, objective);
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }

    @Override
    public Object team(String name, int mode, boolean nameTagVisible, String prefix, String suffix, Collection<String> members) {
        try {
            Object team = this.newTeam(name);
            this.setPrefix.invoke(team, this.component(prefix));
            this.setSuffix.invoke(team, this.component(suffix));
            this.setNameTagVisibility.invoke(team, nameTagVisible ? this.visibilityAlways : this.visibilityNever);
            ((Collection<String>) this.getPlayers.invoke(team)).addAll(members);
            return this.teamAddOrModify.invoke(null, team, mode == TEAM_CREATE);
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }

    @Override
    public Object teamMember(String name, String entry, boolean add) {
        try {
            return this.teamPlayer.invoke(null, this.newTeam(name), entry, add ? this.actionAdd : this.actionRemove);
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }

    @Override
    public Object teamRemove(String name) {
        try {
            return this.teamRemove.invoke(null, this.newTeam(name));
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }

    @Override
    public boolean send(Player player, Object packet) {
        try {
            Object connection = this.connection.get(this.getHandle.invoke(player));
            // The connection is not available yet while the player is still logging in
            if (connection == null)
                return false;
            this.send.invoke(connection, packet);
            return true;
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }
}
