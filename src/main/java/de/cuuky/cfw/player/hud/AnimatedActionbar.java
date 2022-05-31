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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AnimatedActionbar {

    private final StaticActionbar actionbar;
    private final int task;
    private Animation<String> content;
    private boolean contentChanged;

    public AnimatedActionbar(JavaPlugin javaPlugin, Player player, AnimationData<String> content) {
        this.content = new Animation<>(content);
        this.actionbar = new StaticActionbar(player) {
            @Override
            protected String processString(String input) {
                return AnimatedActionbar.this.processString(input);
            }
        };
        this.update();
        this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, this::update, 0, 0);
    }

    public void queueUpdate() {
        this.contentChanged = true;
    }

    private void update() {
        if (this.contentChanged || this.content.shouldUpdate())
            this.actionbar.update(this.content.getCurrentFrame());
        this.content.tick();
    }

    protected String processString(String input) {
        return input;
    }

    public boolean isEnabled() {
        return this.actionbar.isEnabled();
    }

    public void setEnabled(boolean enabled) {
        this.actionbar.setEnabled(enabled);
        this.contentChanged = true;
    }

    public void destroy() {
        Bukkit.getScheduler().cancelTask(this.task);
    }
}
