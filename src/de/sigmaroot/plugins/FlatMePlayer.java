package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.earth2me.essentials.User;

public class FlatMePlayer {

	private FlatMe plugin;
	private UUID uuid;
	private String displayName;
	private Player player;
	private User essentialsUser;
	private List<Integer> plots;
	private PlayerQueue queue;

	public FlatMePlayer(FlatMe plugin, Player player) {
		super();
		this.plugin = plugin;
		this.player = player;
		uuid = player.getUniqueId();
		displayName = player.getDisplayName();
		plots = new ArrayList<Integer>();
		queue = new PlayerQueue(plugin, uuid);
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

	public List<Integer> getPlots() {
		return plots;
	}

	public void setPlots(List<Integer> plots) {
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

	public void setPlayer(Player player) {
		this.player = player;
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
