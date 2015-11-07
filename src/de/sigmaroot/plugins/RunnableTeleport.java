package de.sigmaroot.plugins;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RunnableTeleport {

	private FlatMe plugin;
	private Player player;
	private Location portLocation;
	private boolean isFirst;

	public RunnableTeleport(FlatMe plugin, Player player, Location portLocation, boolean isFirst) {
		super();
		this.plugin = plugin;
		this.player = player;
		this.portLocation = portLocation;
		this.isFirst = isFirst;
	}

	public void run() {
		if (player.hasPermission("flatme.instantport")) {
			runTeleport();
		} else {
			plugin.flatMePlayers.getPlayer(player.getUniqueId()).sendLocalizedString("%willPort%", null);
			Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
				public void run() {
					runTeleport();
				}
			}, 60L);
		}
	}

	public void runTeleport() {
		player.teleport(portLocation);
		if (isFirst) {
			plugin.flatMePlayers.getPlayer(player.getUniqueId()).sendLocalizedString("%teleportToFirstPlot%", null);
		} else {
			plugin.flatMePlayers.getPlayer(player.getUniqueId()).sendLocalizedString("%teleportToPlot%", null);
		}
	}

}
