package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
// import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.earth2me.essentials.User;

public class FlatMePlayer {

	private FlatMe plugin;
	private UUID uuid;
	private String displayName;
	private Player player;
	// private OfflinePlayer offlinePlayer;
	private User essentialsUser;
	private List<Plot> plots;
	private PlayerQueue queue;
	private String[] securityCommand;
	private boolean answeredYes;

	public FlatMePlayer(FlatMe plugin, UUID uuid) {
		super();
		this.plugin = plugin;
		this.uuid = uuid;
		player = plugin.getServer().getPlayer(uuid);
		// offlinePlayer = plugin.getServer().getOfflinePlayer(uuid);
		essentialsUser = plugin.essAPI.getUser(uuid);
		if (essentialsUser != null) {
			displayName = essentialsUser.getName();
		} else {
			displayName = "?";
		}
		plots = new ArrayList<Plot>();
		if (player != null) {
			queue = new PlayerQueue(plugin, uuid, !player.hasPermission("flatme.admin"));
		} else {
			queue = new PlayerQueue(plugin, uuid, true);
		}
		securityCommand = null;
		answeredYes = false;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public User getEssentialsUser() {
		return essentialsUser;
	}

	public void setEssentialsUser(User essentialsUser) {
		this.essentialsUser = essentialsUser;
	}

	public List<Plot> getPlots() {
		return plots;
	}

	public void setPlots(List<Plot> plots) {
		this.plots = plots;
	}

	public PlayerQueue getQueue() {
		return queue;
	}

	public void setQueue(PlayerQueue queue) {
		this.queue = queue;
	}

	public Player getPlayer() {
		return player;
	}

	// public OfflinePlayer getOfflinePlayer() {
	// return offlinePlayer;
	// }
	//
	// public void setOfflinePlayer(OfflinePlayer offlinePlayer) {
	// this.offlinePlayer = offlinePlayer;
	// }

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String[] getSecurityCommand() {
		return securityCommand;
	}

	public void setSecurityCommand(String[] securityCommand) {
		this.securityCommand = securityCommand;
	}

	public void setQueueSilence(boolean isSilence) {
		queue.setSilence(isSilence);
	}

	public boolean isAnsweredYes() {
		return answeredYes;
	}

	public void setAnsweredYes(boolean answeredYes) {
		this.answeredYes = answeredYes;
	}

	public void sendLocalizedString(String message, String args[]) {
		checkForPlayer();
		player.sendMessage(plugin.configurator.resolveLocalizedString(message, args));
	}

	public void checkForPlayer() {
		Player tempPlayer = Bukkit.getServer().getPlayer(uuid);
		if (tempPlayer != null) {
			setPlayer(tempPlayer);
		}
	}

	public void checkForPlots() {
		for (int i = 0; i < plots.size(); i++) {
			if (!plots.get(i).isLocked()) {
				if (plots.get(i).isExpired()) {
					String args[] = { plots.get(i).getPlaceX() + "," + plots.get(i).getPlaceY() };
					RunnableWarning warning = new RunnableWarning(plugin, this, false, args);
					warning.run();
				} else {
					if (plots.get(i).willExpire()) {
						String args[] = { plots.get(i).getPlaceX() + "," + plots.get(i).getPlaceY() };
						RunnableWarning warning = new RunnableWarning(plugin, this, false, args);
						warning.run();
					}
				}
			}
		}
	}

}
