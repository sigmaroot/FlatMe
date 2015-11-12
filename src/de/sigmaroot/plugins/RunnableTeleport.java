package de.sigmaroot.plugins;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RunnableTeleport {

	private FlatMe plugin;
	private Player player;
	private Location portLocation;
	private Location playerLocation;
	private boolean isFirst;

	public RunnableTeleport(FlatMe plugin, Player player, Location portLocation, boolean isFirst) {
		super();
		this.plugin = plugin;
		this.player = player;
		this.portLocation = portLocation;
		playerLocation = player.getLocation();
		this.isFirst = isFirst;
	}

	public void run() {
		if (player.hasPermission("flatme.instantport")) {
			runTeleport();
		} else {
			String[] args = { String.format("%d", plugin.config_portDelay) };
			plugin.flatMePlayers.getPlayer(player.getUniqueId()).sendLocalizedString("%willPort%", args);
			Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
				public void run() {
					runTeleport();
				}
			}, 20L * plugin.config_portDelay);
		}
	}

	public void runTeleport() {
		int oldX = playerLocation.getBlockX();
		int oldY = playerLocation.getBlockY();
		int oldZ = playerLocation.getBlockZ();
		int newX = player.getLocation().getBlockX();
		int newY = player.getLocation().getBlockY();
		int newZ = player.getLocation().getBlockZ();
		if ((oldX == newX) && (oldY == newY) && (oldZ == newZ)) {
			player.teleport(portLocation);
			if (isFirst) {
				plugin.flatMePlayers.getPlayer(player.getUniqueId()).sendLocalizedString("%teleportToFirstPlot%", null);
			} else {
				plugin.flatMePlayers.getPlayer(player.getUniqueId()).sendLocalizedString("%teleportToPlot%", null);
			}
		} else {
			plugin.flatMePlayers.getPlayer(player.getUniqueId()).sendLocalizedString("%teleportAborted%", null);
		}
	}

}
