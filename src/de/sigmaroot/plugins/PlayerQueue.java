package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public class PlayerQueue {

	private FlatMe plugin;
	private List<ChangeBlockEvent> queue;
	private UUID uuid;
	private boolean isRunning;
	private BukkitTask runTaskTimer;
	private int runned;
	private double startSize;
	private boolean isSilence;

	public PlayerQueue(FlatMe plugin, UUID uuid, boolean isSilence) {
		super();
		this.plugin = plugin;
		this.uuid = uuid;
		this.isSilence = isSilence;
		queue = new ArrayList<ChangeBlockEvent>();
		runned = 0;
	}

	public List<ChangeBlockEvent> getQueue() {
		return queue;
	}

	public void setQueue(List<ChangeBlockEvent> queue) {
		this.queue = queue;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
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

	public int getRunned() {
		return runned;
	}

	public void setRunned(int runned) {
		this.runned = runned;
	}

	public double getStartSize() {
		return startSize;
	}

	public void setStartSize(double startSize) {
		this.startSize = startSize;
	}

	public boolean isSilence() {
		return isSilence;
	}

	public void setSilence(boolean isSilence) {
		this.isSilence = isSilence;
	}

	public void addEvent(int x, int y, int z, World world, Material material, byte data, String[] args) {
		if (args != null) {
			queue.add(new ChangeBlockEvent(x, y, z, world, material, data, args));
		} else {
			queue.add(new ChangeBlockEvent(x, y, z, world, material, data, null));
		}
	}

	public double getQueueSize() {
		return queue.size();
	}

	public void run() {
		if (!isSilence) {
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%queueStarted%", null);
		}
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
		if ((!isSilence) && (isRunning)) {
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%queueStopped%", null);
		}
		isRunning = false;
		runned = 0;
		try {
			runTaskTimer.cancel();
		} catch (Exception e) {
			// Timer not started yet
		}
		isSilence = false;
	}

	private void runTask() {
		if (isRunning) {
			for (int i = 0; i < plugin.config.getInt("maxBlocksPerTick", 100); i++) {
				if (getQueueSize() == 0) {
					plugin.getServer().getWorld(plugin.config_world).save();
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
		if (!isSilence) {
			double actual = getQueueSize();
			double done = startSize - actual;
			int percentage = (int) Math.ceil(100 * done / startSize);
			String[] args_2 = { String.format("%,.0f", actual), String.format("%,.0f", done), String.format("%,.0f", startSize), String.format("%d", percentage) };
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%queueSize%", args_2);
		}
	}
}
