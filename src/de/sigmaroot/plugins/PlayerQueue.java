package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public class PlayerQueue {

	private FlatMe plugin;
	private List<ChangeBlockEvent> queue;
	private String playerUUID;
	private boolean isRunning;
	private BukkitTask runTaskTimer;
	private int runned;
	private double startSize;

	public PlayerQueue(FlatMe plugin, String playerUUID) {
		super();
		this.plugin = plugin;
		this.playerUUID = playerUUID;
		queue = new ArrayList<ChangeBlockEvent>();
		runned = 0;
	}

	public List<ChangeBlockEvent> getQueue() {
		return queue;
	}

	public void setQueue(List<ChangeBlockEvent> queue) {
		this.queue = queue;
	}

	public String getPlayerUUID() {
		return playerUUID;
	}

	public void setPlayerUUID(String playerUUID) {
		this.playerUUID = playerUUID;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public BukkitTask getRunTaskTimer() {
		return runTaskTimer;
	}

	public void setRunTaskTimer(BukkitTask runTaskTimer) {
		this.runTaskTimer = runTaskTimer;
	}

	public void addEvent(int x, int y, int z, World world, Material material, byte data) {
		queue.add(new ChangeBlockEvent(x, y, z, world, material, data));
	}

	public double getQueueSize() {
		return queue.size();
	}

	public void run() {
		plugin.flatMePlayers.getPlayer(playerUUID).sendLocalizedString("%queueStarted%", null);
		isRunning = true;
		runned = 0;
		startSize = getQueueSize();
		runTaskTimer = Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			public void run() {
				runTask();
			}
		}, 0, 1);
	}

	public void stop() {
		plugin.flatMePlayers.getPlayer(playerUUID).sendLocalizedString("%queueStopped%", null);
		isRunning = false;
		runned = 0;
		try {
			runTaskTimer.cancel();
		} catch (Exception e) {
			// Timer not started yet
		}
	}

	private void runTask() {
		if (isRunning) {
			for (int i = 0; i < plugin.config.getInt("maxBlocksPerTick", 100); i++) {
				if (getQueueSize() == 0) {
					returnQueueStatus();
					stop();
					return;
				}
				queue.get(0).doEvent();
				queue.remove(0);
			}
			runned++;
			if (runned == 40) {
				returnQueueStatus();
				runned = 0;
			}
		}
	}

	private void returnQueueStatus() {
		double actual = getQueueSize();
		double done = startSize - actual;
		int percentage = (int) Math.ceil(100 * done / startSize);
		String[] args_2 = { Double.toString(actual), Double.toString(done), Double.toString(startSize), Integer.toString(percentage) };
		plugin.flatMePlayers.getPlayer(playerUUID).sendLocalizedString("%queueSize%", args_2);
	}
}
