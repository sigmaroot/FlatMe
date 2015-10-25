package de.sigmaroot.plugins;

import org.bukkit.Material;
import org.bukkit.World;

public class FieldCreator {

	private FlatMe plugin;
	private int radius;
	private World world;
	private FlatMePlayer player;
	private PlayerQueue playerQueue;
	private int plotSize;
	private int lvlHeight;

	public FieldCreator(FlatMe plugin, FlatMePlayer player, int radius, World world) {
		super();
		this.plugin = plugin;
		this.radius = radius;
		this.world = world;
		this.player = player;
		playerQueue = player.getQueue();
		plotSize = plugin.config.getInt("plotSize", 50);
		lvlHeight = plugin.config.getInt("levelHeight", 3);
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getPlotSize() {
		return plotSize;
	}

	public void setPlotSize(int plotSize) {
		this.plotSize = plotSize;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public FlatMePlayer getPlayer() {
		return player;
	}

	public void setPlayer(FlatMePlayer player) {
		this.player = player;
	}

	public int getLvlHeight() {
		return lvlHeight;
	}

	public void setLvlHeight(int lvlHeight) {
		this.lvlHeight = lvlHeight;
	}

	public PlayerQueue getPlayerQueue() {
		return playerQueue;
	}

	public void setPlayerQueue(PlayerQueue playerQueue) {
		this.playerQueue = playerQueue;
	}

	public void runCreate() {
		playerQueue.stop();
		if (radius > 20) {
			player.sendLocalizedString("%commandCreateLarge%", null);
		}
		player.sendLocalizedString("%commandMayTakeAWhile%", null);
		createCenterPoints();
		createWays();
		createBorder();
		player.sendLocalizedString("%commandHasBeenQueued%", null);
	}

	public void runFillUp() {
		player.sendLocalizedString("%commandMayTakeAWhile%", null);
		// TO-DO
		player.sendLocalizedString("%commandHasBeenQueued%", null);
	}

	public void runGenerate() {
		player.sendLocalizedString("%commandMayTakeAWhile%", null);
		// TO-DO
		plugin.commandHandler.executeConsole("mv create test NORMAL -t FLAT");
		player.sendLocalizedString("%commandHasBeenQueued%", null);
	}

	private void createCenterPoints() {
		int jumpInterval = plotSize + 7;
		int lvlHeight = plugin.config.getInt("levelHeight", 3);
		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				int x = j * jumpInterval;
				int y = i * jumpInterval;
				fill((x - 3), (lvlHeight + 1), (y - 3), (x + 3), (lvlHeight + 1), (y + 3), world, Material.AIR, (byte) 0);
				playerQueue.addEvent((x - 3), lvlHeight, (y - 3), world, Material.DIRT, (byte) 0);
				playerQueue.addEvent((x - 3), lvlHeight, (y + 3), world, Material.DIRT, (byte) 0);
				playerQueue.addEvent((x + 3), lvlHeight, (y - 3), world, Material.DIRT, (byte) 0);
				playerQueue.addEvent((x + 3), lvlHeight, (y + 3), world, Material.DIRT, (byte) 0);
				playerQueue.addEvent((x - 3), (lvlHeight + 1), (y - 3), world, Material.STEP, (byte) 7);
				playerQueue.addEvent((x - 3), (lvlHeight + 1), (y + 3), world, Material.STEP, (byte) 7);
				playerQueue.addEvent((x + 3), (lvlHeight + 1), (y - 3), world, Material.STEP, (byte) 7);
				playerQueue.addEvent((x + 3), (lvlHeight + 1), (y + 3), world, Material.STEP, (byte) 7);
				fill((x - 2), lvlHeight, (y - 3), (x + 2), lvlHeight, (y + 3), world, Material.GLOWSTONE, (byte) 0);
				fill((x - 3), lvlHeight, (y - 2), (x + 3), lvlHeight, (y + 2), world, Material.GLOWSTONE, (byte) 0);
				fill((x - 1), lvlHeight, (y - 3), (x + 1), lvlHeight, (y + 3), world, Material.WOOD, (byte) 5);
				fill((x - 3), lvlHeight, (y - 1), (x + 3), lvlHeight, (y + 1), world, Material.WOOD, (byte) 5);
				playerQueue.addEvent((x - 1), lvlHeight, y, world, Material.EMERALD_BLOCK, (byte) 0);
				playerQueue.addEvent((x + 1), lvlHeight, y, world, Material.EMERALD_BLOCK, (byte) 0);
				playerQueue.addEvent(x, lvlHeight, (y - 1), world, Material.EMERALD_BLOCK, (byte) 0);
				playerQueue.addEvent(x, lvlHeight, (y + 1), world, Material.EMERALD_BLOCK, (byte) 0);
				playerQueue.addEvent(x, lvlHeight, y, world, Material.DIAMOND_BLOCK, (byte) 0);
			}
		}

	}

	private void createWays() {
		int jumpInterval = plotSize + 7;
		int lvlHeight = plugin.config.getInt("levelHeight", 3);
		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				if (i < radius) {
					int x = j * jumpInterval;
					int y = i * jumpInterval;
					fill((x - 3), lvlHeight, (y + 4), (x - 3), lvlHeight, (y + plotSize + 3), world, Material.DIRT, (byte) 0);
					fill((x - 3), (lvlHeight + 1), (y + 4), (x - 3), (lvlHeight + 1), (y + plotSize + 3), world, Material.STEP, (byte) 7);
					fill((x + 3), lvlHeight, (y + 4), (x + 3), lvlHeight, (y + plotSize + 3), world, Material.DIRT, (byte) 0);
					fill((x + 3), (lvlHeight + 1), (y + 4), (x + 3), (lvlHeight + 1), (y + plotSize + 3), world, Material.STEP, (byte) 7);
					fill((x - 2), lvlHeight, (y + 4), (x - 2), lvlHeight, (y + plotSize + 3), world, Material.GLOWSTONE, (byte) 0);
					fill((x + 2), lvlHeight, (y + 4), (x + 2), lvlHeight, (y + plotSize + 3), world, Material.GLOWSTONE, (byte) 0);
					fill((x - 1), lvlHeight, (y + 4), (x + 1), lvlHeight, (y + plotSize + 3), world, Material.WOOD, (byte) 5);
				}
				if (j < radius) {
					int x = j * jumpInterval;
					int y = i * jumpInterval;
					fill((x + 4), lvlHeight, (y - 3), (x + plotSize + 3), lvlHeight, (y - 3), world, Material.DIRT, (byte) 0);
					fill((x + 4), (lvlHeight + 1), (y - 3), (x + plotSize + 3), (lvlHeight + 1), (y - 3), world, Material.STEP, (byte) 7);
					fill((x + 4), lvlHeight, (y + 3), (x + plotSize + 3), lvlHeight, (y + 3), world, Material.DIRT, (byte) 0);
					fill((x + 4), (lvlHeight + 1), (y + 3), (x + plotSize + 3), (lvlHeight + 1), (y + 3), world, Material.STEP, (byte) 7);
					fill((x + 4), lvlHeight, (y - 2), (x + plotSize + 3), lvlHeight, (y - 2), world, Material.GLOWSTONE, (byte) 0);
					fill((x + 4), lvlHeight, (y + 2), (x + plotSize + 3), lvlHeight, (y + 2), world, Material.GLOWSTONE, (byte) 0);
					fill((x + 4), lvlHeight, (y - 1), (x + plotSize + 3), lvlHeight, (y + 1), world, Material.WOOD, (byte) 5);
				}
			}
		}
	}

	private void createBorder() {
		int jumpInterval = plotSize + 7;
		int edgeNW = (-1 * radius * jumpInterval) - 3;
		int edgeSE = (radius * jumpInterval) + 3;
		fill(edgeNW, (lvlHeight + 1), edgeNW, edgeSE, (lvlHeight + 1), edgeNW, world, Material.STEP, (byte) 7);
		fill(edgeSE, (lvlHeight + 1), edgeNW, edgeSE, (lvlHeight + 1), edgeSE, world, Material.STEP, (byte) 7);
		fill(edgeSE, (lvlHeight + 1), edgeSE, edgeNW, (lvlHeight + 1), edgeSE, world, Material.STEP, (byte) 7);
		fill(edgeNW, (lvlHeight + 1), edgeSE, edgeNW, (lvlHeight + 1), edgeNW, world, Material.STEP, (byte) 7);
	}

	private void fill(int x1, int y1, int z1, int x2, int y2, int z2, World world, Material material, byte data) {
		int iteratorX = 1;
		if (x2 < x1) {
			iteratorX = -1;
		}
		int iteratorY = 1;
		if (y2 < y1) {
			iteratorY = -1;
		}
		int iteratorZ = 1;
		if (z2 < z1) {
			iteratorZ = -1;
		}
		for (int a = x1; a != (x2 + iteratorX); a += iteratorX) {
			for (int b = y1; b != (y2 + iteratorY); b += iteratorY) {
				for (int c = z1; c != (z2 + iteratorZ); c += iteratorZ) {
					playerQueue.addEvent(a, b, c, world, material, data);
				}
			}
		}
	}

}
