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
	private List<QueueTask> taskQueue;
	private UUID uuid;
	private boolean isRunning;
	private BukkitTask runQueueTimer;
	private BukkitTask checkTaskQueueTimer;
	private double startSize;
	private boolean isSilence;
	private int runnedTimes;
	private int startTaskSize;
	private int blockedForWE;

	public PlayerQueue(FlatMe plugin, UUID uuid, boolean isSilence) {
		super();
		this.plugin = plugin;
		this.uuid = uuid;
		this.isSilence = isSilence;
		queue = new ArrayList<ChangeBlockEvent>();
		taskQueue = new ArrayList<QueueTask>();
		isRunning = false;
		startSize = 0;
		runnedTimes = 0;
		startTaskSize = 0;
		blockedForWE = 0;
	}

	public PlayerQueue(FlatMe plugin) {
		super();
		this.plugin = plugin;
		this.uuid = null;
		isSilence = true;
		queue = new ArrayList<ChangeBlockEvent>();
		taskQueue = new ArrayList<QueueTask>();
		isRunning = false;
		startSize = 0;
		runnedTimes = 0;
		startTaskSize = 0;
		blockedForWE = 0;
	}

	public List<ChangeBlockEvent> getQueue() {
		return queue;
	}

	public void setQueue(List<ChangeBlockEvent> queue) {
		this.queue = queue;
	}

	public List<QueueTask> getTaskQueue() {
		return taskQueue;
	}

	public void setTaskQueue(List<QueueTask> taskQueue) {
		this.taskQueue = taskQueue;
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

	public BukkitTask getRunQueueTimer() {
		return runQueueTimer;
	}

	public void setRunQueueTimer(BukkitTask runQueueTimer) {
		this.runQueueTimer = runQueueTimer;
	}

	public BukkitTask getCheckTaskQueueTimer() {
		return checkTaskQueueTimer;
	}

	public void setCheckTaskQueueTimer(BukkitTask checkTaskQueueTimer) {
		this.checkTaskQueueTimer = checkTaskQueueTimer;
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

	public int getRunnedTimes() {
		return runnedTimes;
	}

	public void setRunnedTimes(int runnedTimes) {
		this.runnedTimes = runnedTimes;
	}

	public int getStartTaskSize() {
		return startTaskSize;
	}

	public void setStartTaskSize(int startTaskSize) {
		this.startTaskSize = startTaskSize;
	}

	public int getBlockedForWE() {
		return blockedForWE;
	}

	public void setBlockedForWE(int blockedForWE) {
		this.blockedForWE = blockedForWE;
	}

	public int getQueueSize() {
		return queue.size();
	}

	public int getTaskQueueSize() {
		return taskQueue.size();
	}

	public void addTask(int posX, int posY, World world, QueueTaskType taskType) {
		QueueTask addTask = null;
		if (uuid == null) {
			addTask = new QueueTask(plugin, null, taskType, posX, posY, world, "I'm alive!");
		} else {
			addTask = new QueueTask(plugin, plugin.flatMePlayers.getPlayer(uuid), taskType, posX, posY, world, "I'm alive!");
		}
		taskQueue.add(addTask);
	}

	public void addTask(int posX, int posY, World world, QueueTaskType taskType, String messageString) {
		QueueTask addTask = null;
		if (uuid == null) {
			addTask = new QueueTask(plugin, null, taskType, posX, posY, world, messageString);
		} else {
			addTask = new QueueTask(plugin, plugin.flatMePlayers.getPlayer(uuid), taskType, posX, posY, world, messageString);
		}
		taskQueue.add(addTask);
	}

	public void runTaskQueue() {
		startTaskSize = getTaskQueueSize();
		if (!isRunning) {
			if (!isSilence) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%queueStarted%", null);
			}
			isRunning = true;
			runnedTimes = 0;
			checkTaskQueueTimer = Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
				public void run() {
					checkForTask();
				}
			}, 20L, 1L);
		}
	}

	public void stopTaskQueue() {
		stopQueue();
		try {
			checkTaskQueueTimer.cancel();
		} catch (Exception e) {
			// Timer not started yet
		}
	}

	public void checkForTask() {
		runnedTimes++;
		if (blockedForWE > 0) {
			blockedForWE--;
		}
		if ((!isSilence) && (runnedTimes >= 100)) {
			returnQueueStatus();
		}
		if (runnedTimes >= 100) {
			runnedTimes = 0;
		}
		if (getQueueSize() == 0) {
			if (getTaskQueueSize() > 0) {
				if (taskQueue.get(0).runTask()) {
					taskQueue.remove(0);
					runQueue();
				}
			} else {
				plugin.getServer().getWorld(plugin.config_world).save();
				if (!isSilence) {
					plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%queueStopped%", null);
				}
				isRunning = false;
				stopTaskQueue();
			}
		}
	}

	public void addEvent(int x, int y, int z, World world, Material material, byte data, String[] args) {
		if (args != null) {
			queue.add(new ChangeBlockEvent(x, y, z, world, material, data, args));
		} else {
			queue.add(new ChangeBlockEvent(x, y, z, world, material, data, null));
		}
	}

	public void runQueue() {
		startSize = getQueueSize();
		runQueueTimer = Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			public void run() {
				runEvent();
			}
		}, 0L, 1L);
	}

	public void stopQueue() {
		try {
			runQueueTimer.cancel();
		} catch (Exception e) {
			// Timer not started yet
		}
	}

	private void runEvent() {
		for (int i = 0; i < plugin.config_maxBlocksPerTick; i++) {
			if (getQueueSize() > 0) {
				queue.get(0).doEvent();
				queue.remove(0);
			}
			if (getQueueSize() == 0) {
				stopQueue();
				return;
			}
		}
	}

	private void returnQueueStatus() {
		double actual = getQueueSize();
		double done = startSize - actual;
		int actualTask = getTaskQueueSize();
		int doneTask = startTaskSize - actualTask;
		int percentage = (int) Math.ceil(100 * done / startSize);
		int percentageTasks = (int) Math.ceil(100 * doneTask / startTaskSize);
		String[] args = { String.format("%,.0f", actual), String.format("%,.0f", done), String.format("%,.0f", startSize), String.format("%d", percentage), String.format("%d", actualTask),
				String.format("%d", percentageTasks) };
		plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%queueSize%", args);
	}
}
