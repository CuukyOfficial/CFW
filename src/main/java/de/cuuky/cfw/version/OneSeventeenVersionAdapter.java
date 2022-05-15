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

package de.cuuky.cfw.version;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;

class OneSeventeenVersionAdapter extends OneSixteenVersionAdapter {

	@Override
	protected void initPlayer() throws NoSuchMethodException, SecurityException, NoSuchFieldException, ClassNotFoundException {
	}

	@Override
	protected void initRespawn() throws ClassNotFoundException {
		// TODO implement this
	}

	protected void initXp() {
		this.initXp("net.minecraft.world.entity.player.EntityHuman", "net.minecraft.world.food.FoodMetaData");
	}

	@Override
	public int getPing(Player player) {
		return player.getPing();
	}

	@Override
	public void respawnPlayer(Player player) {
		throw new Error("Not implemented yet");
	}

	@Override
	protected String getWorldServerFieldName() {
		return "R";
	}

	@Override
	public BlockFace getSignAttachedFace(Block block) {
		BlockData data = block.getBlockData();
		if (data instanceof WallSign)
			return ((WallSign) data).getFacing().getOppositeFace();
		else if (data instanceof Sign)
			return BlockFace.DOWN;
		else
			throw new Error("Block is not a sign: " + block.getLocation());
	}

	@Override
	public Properties getServerProperties() {
		try {
			Method dedicatedServerPropMethod = Bukkit.getServer().getClass().getDeclaredMethod("getProperties");
			dedicatedServerPropMethod.setAccessible(true);
			Object dedicatedServerProp = dedicatedServerPropMethod.invoke(Bukkit.getServer());
			Field[] fields = dedicatedServerProp.getClass().getFields();
			for (Field field : fields) {
				if (!field.getType().equals(Properties.class))
					continue;

				field.setAccessible(true);
				return (Properties) field.get(dedicatedServerProp);
			}
			throw new Error("missing properties field");
		} catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new Error(e);
		}
	}
}
