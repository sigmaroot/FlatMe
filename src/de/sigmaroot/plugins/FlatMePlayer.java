package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.earth2me.essentials.User;

public class FlatMePlayer {

	private FlatMe plugin;
	private UUID uuid;
	private String displayName;
	private Player player;
	private OfflinePlayer offlinePlayer;
	private User essentialsUser;
	private List<Plot> plots;
	private PlayerQueue queue;

	public FlatMePlayer(FlatMe plugin, UUID uuid) {
		super();
		this.plugin = plugin;
		this.uuid = uuid;
		player = plugin.getServer().getPlayer(uuid);
		offlinePlayer = plugin.getServer().getOfflinePlayer(uuid);
		essentialsUser = plugin.essAPI.getUser(uuid);
		displayName = essentialsUser.getName();
		plots = new ArrayList<Plot>();
		queue = new PlayerQueue(plugin, uuid, false);
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

	public OfflinePlayer getOfflinePlayer() {
		return offlinePlayer;
	}

	public void setOfflinePlayer(OfflinePlayer offlinePlayer) {
		this.offlinePlayer = offlinePlayer;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setQueueSilence(boolean isSilence) {
		queue.setSilence(isSilence);
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

}
