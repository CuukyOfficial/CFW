package de.cuuky.cfw.version;

import org.bukkit.entity.Player;

public class CrucibleVersionAdapter extends OneSevenVersionAdapter {
	
	@Override
	protected void initServerPropertys() throws ClassNotFoundException, NoSuchMethodException, SecurityException, NoSuchFieldException {
		this.minecraftServerClass = Class.forName("net.minecraft.server.MinecraftServer");
		this.minecraftServerMethod = this.minecraftServerClass.getMethod("func_71276_C");
		this.propertyManagerMethod = this.minecraftServerClass.getDeclaredMethod("getPropertyManager");
		this.propertiesField = this.propertyManagerMethod.getReturnType().getDeclaredField("field_73672_b");
		this.propertiesField.setAccessible(true);
	}

	@Override
	protected void initPlayer()
			throws NoSuchMethodException, SecurityException, NoSuchFieldException, ClassNotFoundException {
		this.nmsPlayerClass = Class
				.forName("org.bukkit.craftbukkit." + VersionUtils.getNmsVersion() + ".entity.CraftPlayer")
				.getMethod("getHandle").getReturnType();
		this.pingField = this.nmsPlayerClass.getField("field_71138_i");
		this.connectionField = this.nmsPlayerClass.getField("field_71135_a");
		this.sendPacketMethod = this.connectionField.getType().getMethod("func_147359_a",
				Class.forName("net.minecraft.network.Packet"));
	}
	
	@Override
	protected void initRespawn() throws ClassNotFoundException {
		this.enumClientCommandClass = Class.forName("net.minecraft.network.play.client.C16PacketClientStatus$EnumState");
		this.packetPlayInClientCommandClass = Class.forName("net.minecraft.network.play.client.C16PacketClientStatus");
	}

	@Override
	protected void initNetworkManager() throws IllegalArgumentException, IllegalAccessException {
		// TODO Implement this maybe? Or maybe not because the network manager itself should not
		// be used directly by any plugin
	}

	@Override
	protected void initLocale()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		this.localeField = this.nmsPlayerClass.getDeclaredField("field_71148_cg");
		this.localeField.setAccessible(true);
	}

	@Override
	protected void initXp() {
		try {
			Class<?> entityHuman = Class.forName("net.minecraft.entity.player.EntityPlayer");
			this.xpCooldownField = entityHuman.getDeclaredField("field_71090_bL");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public Object getNetworkManager(Player player) {
		throw new Error("Unimplemented");
	}
}
