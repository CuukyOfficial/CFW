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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import de.varoplugin.cfw.player.hud.PacketScoreboardInstance.PacketFactory;

class PacketScoreboardInstanceTest {

    /**
     * Records a readable description of every packet
     */
    private static class FakeFactory implements PacketFactory {

        private final List<String> sent = new ArrayList<>();
        private boolean connected = true;

        @Override
        public Object objective(String name, String title, int mode) {
            return "objective " + title + " " + mode;
        }

        @Override
        public Object sideBar(String objective) {
            return "sidebar";
        }

        @Override
        public Object score(String owner, String objective, int score) {
            return "score " + owner + " " + score;
        }

        @Override
        public Object resetScore(String owner, String objective) {
            return "reset " + owner;
        }

        @Override
        public Object team(String name, int mode, boolean nameTagVisible, String prefix, String suffix, Collection<String> members) {
            return "team " + name + " " + mode + " " + nameTagVisible + " " + prefix + " " + suffix + " " + members;
        }

        @Override
        public Object teamMember(String name, String entry, boolean add) {
            return "member " + name + " " + entry + " " + add;
        }

        @Override
        public Object teamRemove(String name) {
            return "teamremove " + name;
        }

        @Override
        public boolean send(Player player, Object packet) {
            if (!this.connected)
                return false;
            this.sent.add((String) packet);
            return true;
        }
    }

    private static Player player(String name) {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[] { Player.class }, (proxy, method, args) -> {
            if (method.getName().equals("getName"))
                return name;
            if (method.getName().equals("hashCode"))
                return name.hashCode();
            if (method.getName().equals("equals"))
                return proxy == args[0];
            throw new UnsupportedOperationException(method.getName());
        });
    }

    @Test
    void sideBarLifecycle() {
        FakeFactory factory = new FakeFactory();
        PacketScoreboardInstance instance = new PacketScoreboardInstance(player("a"), factory);

        instance.setSideBarScore("ignored", 1); // not registered yet
        instance.registerSideBar();
        instance.setSideBarTitle("T");
        instance.setSideBarScore("line", 3);
        instance.resetSideBarScore("line");
        instance.clearSideBar();
        instance.setSideBarScore("ignored", 1);

        assertEquals(Arrays.asList("objective  0", "sidebar", "objective T 2", "score line 3", "reset line", "objective T 1"), factory.sent);
    }

    @Test
    void nameTagsCreateUpdateAndMove() {
        FakeFactory factory = new FakeFactory();
        PacketScoreboardInstance instance = new PacketScoreboardInstance(player("a"), factory);
        Player target = player("b");

        instance.setNameTag(target, true, "t1", "p", "s");
        instance.setNameTag(target, true, "t1", "p", "s"); // unchanged
        instance.setNameTag(target, false, "t1", "p", "s");
        instance.setNameTag(target, false, "t2", "p", "s"); // moved

        assertEquals(Arrays.asList(
                "team t1 0 true p s []", "member t1 b true",
                "team t1 2 false p s [b]",
                "teamremove t1", "team t2 0 false p s []", "member t2 b true"), factory.sent);
    }

    @Test
    void destroyRemovesEverything() {
        FakeFactory factory = new FakeFactory();
        PacketScoreboardInstance instance = new PacketScoreboardInstance(player("a"), factory);
        instance.registerSideBar();
        instance.setNameTag(player("b"), true, "t1", "", "");
        factory.sent.clear();

        instance.destroy();

        assertEquals(Arrays.asList("objective  1", "teamremove t1"), factory.sent);
    }

    @Test
    void resendsStateAfterPacketsWereLost() {
        FakeFactory factory = new FakeFactory();
        factory.connected = false;
        PacketScoreboardInstance instance = new PacketScoreboardInstance(player("a"), factory);

        instance.registerSideBar();
        instance.setSideBarScore("line", 5);
        instance.setNameTag(player("b"), true, "t1", "p", "s");
        assertTrue(factory.sent.isEmpty());

        factory.connected = true;
        instance.setSideBarScore("other", 1);

        assertEquals(Arrays.asList("objective  0", "sidebar", "score line 5", "team t1 0 true p s [b]", "score other 1"), factory.sent);
    }
}
