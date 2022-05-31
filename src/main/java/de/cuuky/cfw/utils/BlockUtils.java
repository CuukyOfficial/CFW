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

package de.cuuky.cfw.utils;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.version.BukkitVersion;
import de.cuuky.cfw.version.VersionUtils;
import de.cuuky.cfw.version.types.Materials;

public class BlockUtils {

    private static boolean isGrass(Material type) {
        if (!type.toString().contains("GRASS"))
            return false;

        if (VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_12)) {
            return type.toString().contains("GRASS") && !type.toString().contains("BLOCK");
        } else {
            return type.toString().equals("LONG_GRASS");
        }
    }

    public static boolean isAir(Block block) {
        if (block == null)
            return true;

        Material type = block.getType();
        return isGrass(type) || type.name().contains("AIR") || Materials.POPPY.parseMaterial() == type || type == Materials.SUNFLOWER.parseMaterial() || type == Materials.LILY_PAD.parseMaterial() || type.name().contains("LEAVES") || type.name().contains("WOOD") || type == Materials.SNOW.parseMaterial() || type.name().contains("GLASS") || type == Materials.VINE.parseMaterial();
    }

    public static boolean isSame(Materials mat, Block block) {
        if (mat.getData() == block.getData() && mat.parseMaterial().equals(block.getType()))
            return true;

        return false;
    }

    public static void setBlock(Block block, Materials mat, boolean applyPhysics) {
        if (VersionUtils.getVersion().isLowerThan(BukkitVersion.ONE_8))
            block.setType(mat.parseMaterial());
        else
            block.setType(mat.parseMaterial(), applyPhysics);

        if (!VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_11)) {
            try {
                block.getClass().getDeclaredMethod("setData", byte.class).invoke(block, (byte) mat.getData());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setBlock(Block block, Materials mat) {
        setBlock(block, mat, true);
    }

    public static void setBlockDelayed(JavaPlugin plugin, World world, int x, int y, int z, Materials mat) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> setBlock(world.getBlockAt(x, y, z), mat, false), 1);
    }

    public static void setBlockDelayed(JavaPlugin plugin, Block block, Materials mat) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> setBlock(block, mat, false), 1);
    }
}
