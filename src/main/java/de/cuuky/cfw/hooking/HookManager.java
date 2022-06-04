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

package de.cuuky.cfw.hooking;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.hooking.hooks.HookEntity;
import de.cuuky.cfw.hooking.hooks.HookEntityType;
import de.cuuky.cfw.hooking.hooks.item.ItemHook;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;

public class HookManager extends FrameworkManager {

    private final List<HookEntity> hooks;

    public HookManager(JavaPlugin instance) {
        super(FrameworkManagerType.HOOKING, instance);

        this.hooks = new CopyOnWriteArrayList<>();
        this.ownerInstance.getServer().getPluginManager().registerEvents(new HookListener(this), ownerInstance);
    }

    public <B extends HookEntity> B registerHook(B hook) {
        hook.setManager(this);
        hooks.add(hook);
        return hook;
    }

    public boolean unregisterHook(HookEntity hook) {
        return hooks.remove(hook);
    }

    public void clearHooks() {
        for (int i = hooks.size() - 1; i > -1; i--)
            hooks.get(i).unregister();
    }

    public void clearHooks(HookEntityType<?> type) {
        for (int i = hooks.size() - 1; i > -1; i--) {
            HookEntity ent = hooks.get(i);
            if (ent.getType() != type)
                continue;

            hooks.get(i).unregister();
        }
    }

    public <B extends HookEntity> List<B> getHooks(HookEntityType<B> type) {
        return hooks.stream().filter(ent -> ent.getType().getTypeClass().equals(type.getTypeClass())).map(ent -> (B) ent).collect(Collectors.toList());
    }

    public HookEntity getHook(HookEntityType<?> type, Player player) {
        return getHook(type.getTypeClass(), player);
    }

    public <B extends HookEntity> B getHook(Class<B> clazz, Player player) {
        return (B) hooks.stream().filter(ent -> ent.getType().getTypeClass().equals(clazz) && ent.getPlayer().equals(player)).findFirst().orElse(null);
    }

    public ItemHook getItemHook(ItemStack stack, Player player) {
        for (HookEntity ent : hooks)
            if (ent.getType() == HookEntityType.ITEM && ent.getPlayer().equals(player) && ((ItemHook) ent).getItemStack().equals(stack))
                return (ItemHook) ent;

        return null;
    }

    public List<HookEntity> getHooks() {
        return hooks;
    }
}
