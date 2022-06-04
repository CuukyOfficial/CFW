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

package de.cuuky.cfw.hooking.hooks;

import org.bukkit.entity.Player;

import de.cuuky.cfw.hooking.HookManager;

public abstract class HookEntity {

    private HookManager manager;
    private final HookEntityType<?> type;
    private final Player player;

    public HookEntity(HookEntityType<?> type, Player player) {
        this.type = type;
        this.player = player;
    }

    protected abstract HookEntity getExisting(HookManager manager, Player player);

    public void setManager(HookManager manager) {
        HookEntity entity = this.getExisting(manager, this.player);
        if (entity != null)
            entity.unregister();
        this.manager = manager;
    }

    public void unregister() {
        this.manager.unregisterHook(this);
    }

    public HookEntityType<?> getType() {
        return type;
    }

    public Player getPlayer() {
        return this.player;
    }
}
