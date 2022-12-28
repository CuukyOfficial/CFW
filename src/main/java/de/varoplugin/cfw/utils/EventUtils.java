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

package de.varoplugin.cfw.utils;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class EventUtils {

    /**
     * Returns the {@link Player} who damaged another {@link Entity}, regardless of the weapon used.
     * 
     * @param event The event
     * @return The player (possibly {@code null})
     */
    public static Player getDamager(EntityDamageByEntityEvent event) {
        Entity eventDamager = event.getDamager();
        if (eventDamager instanceof Player)
            return (Player) eventDamager;
        if (eventDamager instanceof Arrow) {
            ProjectileSource projectileSource = ((Arrow) eventDamager).getShooter();
            if (projectileSource instanceof Player)
                return (Player) projectileSource;
            return null;
        }
        return null;
    }
}
