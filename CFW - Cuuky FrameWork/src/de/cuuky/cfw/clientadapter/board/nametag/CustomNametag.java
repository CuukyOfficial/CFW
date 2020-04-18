package de.cuuky.cfw.clientadapter.board.nametag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.cuuky.cfw.clientadapter.board.CustomBoard;
import de.cuuky.cfw.clientadapter.board.CustomBoardType;
import de.cuuky.cfw.player.CustomPlayer;
import de.cuuky.cfw.version.BukkitVersion;
import de.cuuky.cfw.version.VersionUtils;

public class CustomNametag extends CustomBoard {

	private static Class<?> visibilityClass;
	private static Method setVisibilityMethod, getVisibilityMethod;

	static {
		if(VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_7)) {
			try {
				visibilityClass = Class.forName("org.bukkit.scoreboard.NameTagVisibility");

				setVisibilityMethod = Team.class.getDeclaredMethod("setNameTagVisibility", visibilityClass);
				getVisibilityMethod = Team.class.getDeclaredMethod("getNameTagVisibility");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String name, prefix, suffix, oldName;
	private boolean init, visibilityEnabled;
	private Object visibility;

	public CustomNametag(CustomPlayer player) {
		super(CustomBoardType.NAMETAG, player);
	}

	@Override
	public void onEnable() {
		this.visibilityEnabled = true;

		startDelayedRefresh();
	}

	private void startDelayedRefresh() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(this.manager.getInstance().getPluginInstance(), new Runnable() {

			@Override
			public void run() {
				init = true;

				refreshPrefix();
				giveAll();
			}
		}, 1);
	}

	private boolean refreshPrefix() {
		String newName = this.getUpdateHandler().getNametagName(player.getPlayer());
		String newPrefix = this.getUpdateHandler().getNametagPrefix(player.getPlayer());
		String newSuffix = this.getUpdateHandler().getNametagSuffix(player.getPlayer());
		if(newName.length() > 16)
			newName = newName.substring(0, 16);

		if(newPrefix.length() > 16)
			newPrefix = newPrefix.substring(0, 16);

		if(newSuffix.length() > 16)
			newSuffix = newSuffix.substring(0, 16);

		boolean changed = name == null || !newName.equals(name) || !newPrefix.equals(prefix) || !newSuffix.equals(suffix);
		if(!changed)
			return false;

		this.oldName = this.name;
		this.name = newName;
		this.prefix = newPrefix;
		this.suffix = newSuffix;

		return changed;
	}

	private Object getVisibility(Team team) {
		try {
			return getVisibilityMethod.invoke(team);
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void setVisibility(Team team) {
		if(!visibilityEnabled)
			return;

		if(visibility == null) {
			try {
				this.visibility = this.getUpdateHandler().isNametagVisible(player.getPlayer()) ? visibilityClass.getDeclaredField("NEVER").get(null) : visibilityClass.getDeclaredField("ALWAYS").get(null);
			} catch(Exception e) {
				this.visibilityEnabled = false;
				return;
			}
		}

		if(getVisibility(team).equals(visibility))
			return;

		try {
			setVisibilityMethod.invoke(team, visibility);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private void updateFor(Scoreboard board, CustomNametag nametag) {
		if(nametag.getName() == null)
			return;

		if(nametag.getOldName() != null) {
			Team oldTeam = board.getTeam(nametag.getOldName());

			if(oldTeam != null)
				oldTeam.unregister();
		}

		Team team = board.getTeam(nametag.getName());

		if(team == null) {
			team = board.registerNewTeam(nametag.getName());
			team.addPlayer(nametag.getPlayer().getPlayer());
		}

		setVisibility(team);
		if(nametag.getPrefix() != null) {
			if(team.getPrefix() == null)
				team.setPrefix(nametag.getPrefix());
			else if(!team.getPrefix().equals(nametag.getPrefix()))
				team.setPrefix(nametag.getPrefix());
		}

		if(nametag.getSuffix() != null) {
			if(team.getSuffix() == null)
				team.setSuffix(nametag.getSuffix());
			else if(!team.getSuffix().equals(nametag.getSuffix()))
				team.setSuffix(nametag.getSuffix());
		}
	}

	@Override
	protected void onUpdate() {
		if(!init || !refreshPrefix())
			return;

		setToAll();
		giveAll();
	}

	public void giveAll() {
		Scoreboard board = player.getPlayer().getScoreboard();
		for(CustomBoard nametag : this.manager.getBoards(CustomBoardType.NAMETAG)) 
			updateFor(board, (CustomNametag) nametag);
	}

	public void setToAll() {
		for(Player toSet : Bukkit.getOnlinePlayers())
			updateFor(toSet.getScoreboard(), this);
	}

	public String getPrefix() {
		return this.prefix;
	}

	public String getName() {
		return this.name;
	}

	public String getSuffix() {
		return this.suffix;
	}

	public String getOldName() {
		return oldName;
	}
}