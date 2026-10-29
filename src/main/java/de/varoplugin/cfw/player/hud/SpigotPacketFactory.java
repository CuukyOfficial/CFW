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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.varoplugin.cfw.player.hud.PacketScoreboardInstance.PacketFactory;

/**
 * Builds packets on servers that use the Spigot mappings at runtime (Folia 1.19.4 - 1.20.4). Only the names of classes are used, as the names
 * of methods and fields are obfuscated. Instead of using the vanilla scoreboard classes, the packets are created from their serialized form
 * (every packet has a constructor that reads from a buffer). Only chat components are serialized by the server, because their format changed
 * in 1.20.3.
 */
class SpigotPacketFactory implements PacketFactory {

    private static final String NMS = "net.minecraft.";
    private static final String PACKETS = NMS + "network.protocol.game.";
    private static final int SIDEBAR_SLOT = 1;
    private static final int NO_TEAM_COLOR = 21;
    private static final int TEAM_REMOVE = 1, TEAM_ADD_PLAYERS = 3, TEAM_REMOVE_PLAYERS = 4;
    private static final int SCORE_CHANGE = 0, SCORE_REMOVE = 1;

    private final Method fromString;
    private final Method fromJson;
    private final Method getHandle;
    private final Field connection;
    private final Method send;
    private final Constructor<?> serializer;
    private final Method writeComponent;
    private final Method wrappedBuffer;
    private final Method newBuffer;
    private final Method readableBytes;
    private final Method readBytes;
    private final Constructor<?> objectivePacket, displayPacket, scorePacket, resetScorePacket, teamPacket;
    /** Since 1.20.2 the display slot is a VarInt instead of a byte */
    private final boolean slotVarInt;
    /** Since 1.20.3 scores are reset using a separate packet and objectives and scores may have a number format */
    private final boolean newScores;

    SpigotPacketFactory() throws ReflectiveOperationException {
        String craftPackage = Bukkit.getServer().getClass().getPackage().getName();
        Class<?> packet = Class.forName(NMS + "network.protocol.Packet");
        Class<?> component = Class.forName(NMS + "network.chat.IChatBaseComponent");

        this.fromString = Class.forName(craftPackage + ".util.CraftChatMessage").getMethod("fromStringOrNull", String.class);
        this.fromJson = this.fromString.getDeclaringClass().getMethod("fromJSON", String.class);
        this.getHandle = Class.forName(craftPackage + ".entity.CraftPlayer").getMethod("getHandle");

        // All names below are obfuscated, find the members by their types
        Class<?> connectionClass = Class.forName(NMS + "server.network.PlayerConnection");
        this.connection = findField(this.getHandle.getReturnType(), connectionClass);
        this.send = findMethod(connectionClass, packet);

        Class<?> serializerClass = Class.forName(NMS + "network.PacketDataSerializer");
        Class<?> byteBuf = Class.forName("io.netty.buffer.ByteBuf");
        this.serializer = serializerClass.getConstructor(byteBuf);
        this.writeComponent = findMethod(serializerClass, component);
        Class<?> unpooled = Class.forName("io.netty.buffer.Unpooled");
        this.wrappedBuffer = unpooled.getMethod("wrappedBuffer", byte[].class);
        this.newBuffer = unpooled.getMethod("buffer");
        this.readableBytes = byteBuf.getMethod("readableBytes");
        this.readBytes = byteBuf.getMethod("readBytes", byte[].class);

        this.objectivePacket = Class.forName(PACKETS + "PacketPlayOutScoreboardObjective").getConstructor(serializerClass);
        this.displayPacket = Class.forName(PACKETS + "PacketPlayOutScoreboardDisplayObjective").getConstructor(serializerClass);
        this.scorePacket = Class.forName(PACKETS + "PacketPlayOutScoreboardScore").getConstructor(serializerClass);
        this.teamPacket = Class.forName(PACKETS + "PacketPlayOutScoreboardTeam").getConstructor(serializerClass);

        Constructor<?> reset = null;
        try {
            reset = Class.forName(PACKETS + "ClientboundResetScorePacket").getConstructor(serializerClass);
        } catch (ClassNotFoundException e) {
            // before 1.20.3
        }
        this.resetScorePacket = reset;
        this.newScores = reset != null;

        boolean varInt;
        try {
            Class.forName(NMS + "world.scores.DisplaySlot");
            varInt = true;
        } catch (ClassNotFoundException e) {
            varInt = false;
        }
        this.slotVarInt = varInt;
    }

    private static Field findField(Class<?> owner, Class<?> type) throws NoSuchFieldException {
        for (Field field : owner.getFields())
            if (field.getType() == type && !Modifier.isStatic(field.getModifiers()))
                return field;
        throw new NoSuchFieldException(type.getName() + " in " + owner.getName());
    }

    /**
     * @return The public non-static void method of the class that has exactly one parameter of the given type
     */
    private static Method findMethod(Class<?> owner, Class<?> parameter) throws NoSuchMethodException {
        for (Method method : owner.getMethods())
            if (!Modifier.isStatic(method.getModifiers()) && method.getParameterCount() == 1 && method.getParameterTypes()[0] == parameter
                    && (method.getReturnType() == void.class || method.getReturnType() == owner))
                return method;
        throw new NoSuchMethodException(owner.getName() + "(" + parameter.getName() + ")");
    }

    /**
     * Writes data in the format of the network protocol
     */
    private class Buffer {

        private final ByteArrayOutputStream out = new ByteArrayOutputStream();

        Buffer varInt(int value) {
            while ((value & -128) != 0) {
                this.out.write(value & 127 | 128);
                value >>>= 7;
            }
            this.out.write(value);
            return this;
        }

        Buffer bool(boolean value) {
            this.out.write(value ? 1 : 0);
            return this;
        }

        Buffer bytes(int value) {
            this.out.write(value);
            return this;
        }

        Buffer string(String value) {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            this.varInt(bytes.length);
            this.out.write(bytes, 0, bytes.length);
            return this;
        }

        Buffer component(String legacyText) throws ReflectiveOperationException {
            Object buffer = SpigotPacketFactory.this.newBuffer.invoke(null);
            Object writer = SpigotPacketFactory.this.serializer.newInstance(buffer);
            Object component = SpigotPacketFactory.this.fromString.invoke(null, legacyText);
            if (component == null) // empty text
                component = SpigotPacketFactory.this.fromJson.invoke(null, "{\"text\":\"\"}");
            SpigotPacketFactory.this.writeComponent.invoke(writer, component);
            byte[] bytes = new byte[(int) SpigotPacketFactory.this.readableBytes.invoke(buffer)];
            SpigotPacketFactory.this.readBytes.invoke(buffer, (Object) bytes);
            try {
                this.out.write(bytes);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            return this;
        }

        Object build(Constructor<?> packet) throws ReflectiveOperationException {
            return packet.newInstance(SpigotPacketFactory.this.newSerializer(this.out.toByteArray()));
        }
    }

    private Object newSerializer(byte[] content) throws ReflectiveOperationException {
        return this.serializer.newInstance(this.wrappedBuffer.invoke(null, (Object) content));
    }

    @Override
    public Object objective(String name, String title, int mode) {
        try {
            Buffer buffer = new Buffer().string(name).bytes(mode);
            if (mode != OBJECTIVE_REMOVE) {
                buffer.component(title).varInt(0); // render type: integer
                if (this.newScores)
                    buffer.bool(false); // number format
            }
            return buffer.build(this.objectivePacket);
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }

    @Override
    public Object sideBar(String objective) {
        try {
            Buffer buffer = new Buffer();
            if (this.slotVarInt)
                buffer.varInt(SIDEBAR_SLOT);
            else
                buffer.bytes(SIDEBAR_SLOT);
            return buffer.string(objective).build(this.displayPacket);
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }

    @Override
    public Object score(String owner, String objective, int score) {
        try {
            if (this.newScores)
                return new Buffer().string(owner).string(objective).varInt(score).bool(false).bool(false).build(this.scorePacket);
            return new Buffer().string(owner).varInt(SCORE_CHANGE).string(objective).varInt(score).build(this.scorePacket);
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }

    @Override
    public Object resetScore(String owner, String objective) {
        try {
            if (this.newScores)
                return new Buffer().string(owner).bool(true).string(objective).build(this.resetScorePacket);
            return new Buffer().string(owner).varInt(SCORE_REMOVE).string(objective).build(this.scorePacket);
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }

    @Override
    public Object team(String name, int mode, boolean nameTagVisible, String prefix, String suffix, Collection<String> members) {
        try {
            Buffer buffer = new Buffer().string(name).bytes(mode);
            buffer.component(name); // display name
            buffer.bytes(3); // allow friendly fire and see friendly invisibles
            buffer.string(nameTagVisible ? "always" : "never");
            buffer.string("always"); // collision rule
            buffer.varInt(NO_TEAM_COLOR);
            buffer.component(prefix).component(suffix);
            if (mode == TEAM_CREATE) {
                buffer.varInt(members.size());
                for (String member : members)
                    buffer.string(member);
            }
            return buffer.build(this.teamPacket);
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }

    @Override
    public Object teamMember(String name, String entry, boolean add) {
        try {
            return new Buffer().string(name).bytes(add ? TEAM_ADD_PLAYERS : TEAM_REMOVE_PLAYERS).varInt(1).string(entry).build(this.teamPacket);
        } catch (ReflectiveOperationException e) {
            throw Reflection.rethrow(e);
        }
    }

    @Override
    public Object teamRemove(String name) {
        try {
            return new Buffer().string(name).bytes(TEAM_REMOVE).build(this.teamPacket);
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
