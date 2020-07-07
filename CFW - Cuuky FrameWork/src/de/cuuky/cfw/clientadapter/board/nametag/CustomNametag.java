package de.cuuky.cfw.clientadapter.board.nametag;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.cuuky.cfw.clientadapter.board.CustomBoard;
import de.cuuky.cfw.clientadapter.board.CustomBoardType;
import de.cuuky.cfw.player.CustomPlayer;
import de.cuuky.cfw.version.BukkitVersion;
import de.cuuky.cfw.version.VersionUtils;

public class CustomNametag<T extends CustomPlayer> extends CustomBoard<T> {

	private static Class<?> visibilityClass;
	private static Method setVisibilityMethod;
	private static Object visibilityNever, visibilityAlways;

	static {
		if (VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_7)) {
			try {
				visibilityClass = Class.forName("org.bukkit.scoreboard.NameTagVisibility");
				visibilityNever = visibilityClass.getDeclaredField("NEVER").get(null);
				visibilityAlways = visibilityClass.getDeclaredField("ALWAYS").get(null);
				setVisibilityMethod = Team.class.getDeclaredMethod("setNameTagVisibility", visibilityClass);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String name, prefix, suffix, oldName;
	private boolean initalized, nametagShown;

	public CustomNametag(T player) {
		super(CustomBoardType.NAMETAG, player);
	}

	@Override
	public void onEnable() {
		this.nametagShown = true;
		Scoreboard sb = player.getPlayer().getScoreboard();
		if (sb.getTeams().size() > 0)
			for (int i = sb.getTeams().size() - 1; i != 0; i--) {
				Team team = (Team) sb.getTeams().toArray()[i];
				try {
					if (!team.getName().startsWith("team-"))
						team.unregister();
				} catch (IllegalStateException e) {}
			}

		giveAll();
		this.initalized = true;
	}

	public void startDelayedRefresh() {
		this.manager.getOwnerInstance().getServer().getScheduler().scheduleSyncDelayedTask(this.manager.getOwnerInstance(), new Runnable() {

			@Override
			public void run() {
				update();
			}
		}, 1);
	}

	private boolean refreshPrefix() {
		String newName = this.getUpdateHandler().getNametagName(player);
		if (newName.startsWith("team-"))
			throw new IllegalArgumentException("Player nametag name cannot start with 'team-'");

		String newPrefix = this.getUpdateHandler().getNametagPrefix(player), newSuffix = this.getUpdateHandler().getNametagSuffix(player);
		if (newName.length() > 16)
			newName = newName.substring(0, 16);

		if (newPrefix.length() > 16)
			newPrefix = newPrefix.substring(0, 16);

		if (newSuffix.length() > 16)
			newSuffix = newSuffix.substring(0, 16);

		boolean newNametagShown = this.getUpdateHandler().isNametagVisible(player);
		boolean changed = name == null || !newName.equals(name) || !newPrefix.equals(prefix) || !newSuffix.equals(suffix) || newNametagShown != this.nametagShown;
		if (!changed)
			return false;

		this.oldName = this.name;
		this.name = newName;
		this.prefix = newPrefix;
		this.suffix = newSuffix;
		this.nametagShown = newNametagShown;

		return true;
	}

	@SuppressWarnings("deprecation")
	private void updateFor(Scoreboard board, CustomNametag<T> nametag) {
		if (nametag.getName() == null)
			return;

		if (nametag.getOldName() != null) {
			Team oldTeam = board.getTeam(nametag.getOldName());

			if (oldTeam != null)
				oldTeam.unregister();
		}

		Team team = board.getTeam(nametag.getName());
		if (team == null) {
			team = board.registerNewTeam(nametag.getName());
			team.addPlayer(nametag.getPlayer().getPlayer());
		}

		setVisibility(team, nametag.isNametagShown());
		if (nametag.getPrefix() != null) {
			if (team.getPrefix() == null)
				team.setPrefix(nametag.getPrefix());
			else if (!team.getPrefix().equals(nametag.getPrefix()))
				team.setPrefix(nametag.getPrefix());
		}

		if (nametag.getSuffix() != null) {
			if (team.getSuffix() == null)
				team.setSuffix(nametag.getSuffix());
			else if (!team.getSuffix().equals(nametag.getSuffix()))
				team.setSuffix(nametag.getSuffix());
		}
	}

	@Override
	protected void onUpdate() {
		if (!refreshPrefix())
			return;

		setToAll();
	}

	public void giveAll() {
		Scoreboard board = player.getPlayer().getScoreboard();
		for (CustomBoard<T> nametag : this.manager.getBoards(CustomBoardType.NAMETAG))
			if (((CustomNametag<T>) nametag).isInitalized())
				updateFor(board, (CustomNametag<T>) nametag);
	}

	public void setToAll() {
		for (Player toSet : VersionUtils.getOnlinePlayer())
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

	public boolean isNametagShown() {
		return nametagShown;
	}

	public boolean isInitalized() {
		return initalized;
	}

	private static void setVisibility(Team team, boolean shown) {
		if (!VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_7))
			return;

		try {
			setVisibilityMethod.invoke(team, shown ? visibilityAlways : visibilityNever);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}