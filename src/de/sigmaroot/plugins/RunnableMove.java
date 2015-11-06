package de.sigmaroot.plugins;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class RunnableMove {

	private FlatMe plugin;
	private Player movePlayer;
	private int pos1x;
	private int pos1y;
	private int pos2x;
	private int pos2y;
	private World moveWorld;

	public RunnableMove(FlatMe plugin, Player movePlayer, int pos1x, int pos1y, int pos2x, int pos2y, World moveWorld) {
		super();
		this.plugin = plugin;
		this.movePlayer = movePlayer;
		this.pos1x = pos1x;
		this.pos1y = pos1y;
		this.pos2x = pos2x;
		this.pos2y = pos2y;
		this.moveWorld = moveWorld;
	}

	public void run() {
		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				runStep1();
			}
		}, 100L);
		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				runStep2();
			}
		}, 200L);
		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				runStep3();
			}
		}, 300L);
		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				runStep4();
			}
		}, 400L);
		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				runStep5();
			}
		}, 500L);
	}

	public void runStep1() {
		int portX = pos1x * plugin.config_jumpInterval;
		int portY = pos1y * plugin.config_jumpInterval;
		Location portLocation = new Location(moveWorld, portX, (plugin.config_lvlHeight + 1), portY);
		portLocation.setYaw(-45F);
		movePlayer.teleport(portLocation);
	}

	public void runStep2() {
		BlockChanger blockChanger = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(movePlayer.getUniqueId()), moveWorld);
		blockChanger.runWEcopy(pos1x, pos1y);
	}

	public void runStep3() {
		BlockChanger blockChanger = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(movePlayer.getUniqueId()), moveWorld);
		blockChanger.runWEregen(pos1x, pos1y);
	}

	public void runStep4() {
		int portX = pos2x * plugin.config_jumpInterval;
		int portY = pos2y * plugin.config_jumpInterval;
		Location portLocation = new Location(moveWorld, portX, (plugin.config_lvlHeight + 1), portY);
		portLocation.setYaw(-45F);
		movePlayer.teleport(portLocation);
	}

	public void runStep5() {
		BlockChanger blockChanger = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(movePlayer.getUniqueId()), moveWorld);
		blockChanger.runWEpaste(pos2x, pos2y);
	}

}
