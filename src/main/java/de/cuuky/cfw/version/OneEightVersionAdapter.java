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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import net.md_5.bungee.api.chat.ClickEvent;

@SuppressWarnings("deprecation")
class OneEightVersionAdapter extends OneSevenVersionAdapter {

	protected Class<?> chatBaseComponentInterface;
	protected Method chatSerializerMethod;

	// chat
	protected Class<?> packetChatClass;
	protected Constructor<?> packetChatConstructor;
	private Constructor<?> titleConstructor;
	private Object title, subtitle;

	// tablist
	private Class<?> tablistPacketClass;
	private Field footerField, headerField;

	// nbt
	private Class<?> netTagClass;
	private Method nbtSetByteMethod, initNbtMethod, loadNbtMethod;

	@Override
	protected void init() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException, ClassNotFoundException {
		super.init();
		this.initTablist();
		this.initChat();
		this.initTitle();
		this.initNbt();
	}

	@Override
	protected void initRespawn() throws ClassNotFoundException {
		// TODO
	}

	protected void initTablist() throws NoSuchFieldException, SecurityException, ClassNotFoundException, NoSuchMethodException {
		this.tablistPacketClass = Class.forName(VersionUtils.getNmsClass() + ".PacketPlayOutPlayerListHeaderFooter");

		this.headerField = this.tablistPacketClass.getDeclaredField("a");
		this.headerField.setAccessible(true);

		this.footerField = this.tablistPacketClass.getDeclaredField("b");
		this.footerField.setAccessible(true);
	}

	protected void initChat() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		this.chatBaseComponentInterface = Class.forName(VersionUtils.getNmsClass() + ".IChatBaseComponent");
		Class<?> chatSerializer = Class.forName(VersionUtils.getNmsClass() + ".IChatBaseComponent$ChatSerializer"); // .ChatSerializer
																													// //.network.chat.IChatBaseComponent$ChatSerializer
		this.chatSerializerMethod = chatSerializer.getDeclaredMethod("a", String.class);

		this.packetChatClass = Class.forName(VersionUtils.getNmsClass() + ".PacketPlayOutChat");
		this.initPacketChatArgConstructor();
	}

	protected void initPacketChatArgConstructor() throws NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		this.packetChatConstructor = this.packetChatClass.getConstructor(this.chatBaseComponentInterface, byte.class);
	}

	protected void initTitle() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, NoSuchMethodException {
		Class<?> titleClass = Class.forName(VersionUtils.getNmsClass() + ".PacketPlayOutTitle");
		Class<?> enumTitleClass = titleClass.getDeclaredClasses()[0]; // Class.forName(path + ".EnumTitleAction");
		this.titleConstructor = titleClass.getConstructor(enumTitleClass, this.chatBaseComponentInterface, int.class, int.class, int.class);
		this.title = enumTitleClass.getDeclaredField("TITLE").get(null);
		this.subtitle = enumTitleClass.getDeclaredField("SUBTITLE").get(null);
	}

	protected void initNbt() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		this.netTagClass = Class.forName(VersionUtils.getNmsClass() + ".NBTTagCompound");
		this.nbtSetByteMethod = this.netTagClass.getDeclaredMethod("setByte", String.class, byte.class);

		Class<?> entityClass = Class.forName(VersionUtils.getNmsClass() + ".Entity");
		this.initNbtMethod = entityClass.getMethod("c", this.netTagClass);
		this.loadNbtMethod = entityClass.getMethod("f", this.netTagClass);
	}

	@Override
	public void respawnPlayer(Player player) {
		throw new Error("Unimplemented");
	}

	@Override
	public void sendActionbar(Player player, String message, int duration, Plugin instance) {
		new BukkitRunnable() {

			private int count;

			@Override
			public void run() {
				OneEightVersionAdapter.this.sendActionbar(player, message);

				if (this.count >= duration)
					this.cancel();

				this.count++;
			}
		}.runTaskTimerAsynchronously(instance, 0, 20);
	}

	@Override
	public void sendActionbar(Player player, String message) {
		try {
			Object barchat = this.chatSerializerMethod.invoke(null, "{\"text\": \"" + message + "\"}");
			this.sendPacket(player, this.getActionbarPacket(player, barchat));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected Object getActionbarPacket(Player player, Object text) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return this.packetChatConstructor.newInstance(text, (byte) 2);
	}

	@Override
	public void sendClickableMessage(Player player, String message, ClickEvent.Action action, String value) {
		try {
			Object text = this.chatSerializerMethod.invoke(null, "{\"text\": \"" + message + "\", \"color\": \"white\", \"clickEvent\": {\"action\": \"" + action.name().toLowerCase(Locale.ROOT) + "\" , \"value\": \"" + value + "\"}}");
			this.sendPacket(player, this.getMessagePacket(player, text));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	protected Object getMessagePacket(Player player, Object text) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return this.packetChatConstructor.newInstance(text, (byte) 1);
	}

	@Override
	public void sendTablist(Player player, String header, String footer) {
		try {
			Object tabheader = this.chatSerializerMethod.invoke(null, "{\"text\": \"" + header + "\"}");
			Object tabfooter = this.chatSerializerMethod.invoke(null, "{\"text\": \"" + footer + "\"}");
			Object packet = this.tablistPacketClass.newInstance();

			this.headerField.set(packet, tabheader);
			this.footerField.set(packet, tabfooter);

			this.sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendTitle(Player player, String title, String subtitle) {
		try {
			Object titleHeader = this.chatSerializerMethod.invoke(null, "{\"text\": \"" + title + "\"}");
			Object titleFooter = this.chatSerializerMethod.invoke(null, "{\"text\": \"" + subtitle + "\"}");

			Object headerPacket = this.titleConstructor.newInstance(this.title, titleHeader, 0, 2, 0);
			Object footerPacket = this.titleConstructor.newInstance(this.subtitle, titleFooter, 0, 2, 0);

			this.sendPacket(player, headerPacket);
			this.sendPacket(player, footerPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setNametagVisibility(Team team, boolean shown) {
		team.setNameTagVisibility(shown ? NameTagVisibility.ALWAYS : NameTagVisibility.NEVER);
	}

	@Override
	public void setArmorstandAttributes(Entity armorstand, boolean visible, boolean customNameVisible, boolean gravity, String customName) {
		ArmorStand stand = (ArmorStand) armorstand;
		stand.setVisible(visible);
		if (!visible)
			try {
				stand.setMarker(true);
			} catch (NoSuchMethodError e) {
				// unsupported in 1.8.0
			}

		stand.setCustomNameVisible(customNameVisible);
		stand.setGravity(gravity);

		if (stand.getCustomName() == null) {
			if (customName != null)
				stand.setCustomName(customName);
		} else if (!stand.getCustomName().equals(customName))
			stand.setCustomName(customName);
	}

	@Override
	public void removeAi(LivingEntity entity) {
		try {
			Object handle = this.getHandle(entity);
			Object compound = this.netTagClass.newInstance();

			this.initNbtMethod.invoke(handle, compound); // nms.Entity#c
			this.nbtSetByteMethod.invoke(compound, "NoAI", (byte) 1);
			this.loadNbtMethod.invoke(handle, compound); // nms.Entity#load
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			throw new Error(e);
		}
	}

	@Override
	public void deleteItemAnnotations(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.values());
		item.setItemMeta(meta);
	}
}
