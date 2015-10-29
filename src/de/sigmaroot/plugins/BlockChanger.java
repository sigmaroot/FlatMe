package de.sigmaroot.plugins;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;

public class BlockChanger {

	private FlatMe plugin;
	private World world;
	private FlatMePlayer player;
	private PlayerQueue playerQueue;

	public BlockChanger(FlatMe plugin, FlatMePlayer player, World world) {
		super();
		this.plugin = plugin;
		this.world = world;
		this.player = player;
		playerQueue = player.getQueue();
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

	public PlayerQueue getPlayerQueue() {
		return playerQueue;
	}

	public void setPlayerQueue(PlayerQueue playerQueue) {
		this.playerQueue = playerQueue;
	}

	public void runCreate() {
		playerQueue.stop();
		if (plugin.config_radius > 20) {
			player.sendLocalizedString("%commandCreateLarge%", null);
		}
		player.sendLocalizedString("%commandMayTakeAWhile%", null);
		createCenterPoints();
		createWays();
		createFieldBorder();
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

	public void runPlot(int posX, int posY, boolean used, boolean expired, boolean locked) {
		if (!used) {
			createPlotBorder(posX, posY, (byte) 7);
		} else {
			if (!expired) {
				if (!locked) {
					createPlotBorder(posX, posY, (byte) 6);
				} else {
					createPlotBorder(posX, posY, (byte) 0);
				}
			} else {
				if (!locked) {
					createPlotBorder(posX, posY, (byte) 4);
				} else {
					createPlotBorder(posX, posY, (byte) 0);
				}
			}
		}
	}

	public void runRegen(int posX, int posY) {
		fill(((posX * plugin.config_jumpInterval) + 4), 1, ((posY * plugin.config_jumpInterval) + 4), (((posX + 1) * plugin.config_jumpInterval) - 4), 2,
				(((posY + 1) * plugin.config_jumpInterval) - 4), world, Material.DIRT, (byte) 0);
		fill(((posX * plugin.config_jumpInterval) + 4), 3, ((posY * plugin.config_jumpInterval) + 4), (((posX + 1) * plugin.config_jumpInterval) - 4), 3,
				(((posY + 1) * plugin.config_jumpInterval) - 4), world, Material.GRASS, (byte) 0);
		fill(((posX * plugin.config_jumpInterval) + 4), 4, ((posY * plugin.config_jumpInterval) + 4), (((posX + 1) * plugin.config_jumpInterval) - 4), (world.getMaxHeight() - 1),
				(((posY + 1) * plugin.config_jumpInterval) - 4), world, Material.AIR, (byte) 0);
	}

	private void createCenterPoints() {
		for (int i = -plugin.config_radius; i <= plugin.config_radius; i++) {
			for (int j = -plugin.config_radius; j <= plugin.config_radius; j++) {
				int x = j * plugin.config_jumpInterval;
				int y = i * plugin.config_jumpInterval;
				fill((x - 3), (plugin.config_lvlHeight + 1), (y - 3), (x + 3), (plugin.config_lvlHeight + 1), (y + 3), world, Material.AIR, (byte) 0);
				playerQueue.addEvent((x - 3), plugin.config_lvlHeight, (y - 3), world, Material.DIRT, (byte) 0, null);
				playerQueue.addEvent((x - 3), plugin.config_lvlHeight, (y + 3), world, Material.DIRT, (byte) 0, null);
				playerQueue.addEvent((x + 3), plugin.config_lvlHeight, (y - 3), world, Material.DIRT, (byte) 0, null);
				playerQueue.addEvent((x + 3), plugin.config_lvlHeight, (y + 3), world, Material.DIRT, (byte) 0, null);
				playerQueue.addEvent((x - 3), (plugin.config_lvlHeight + 1), (y - 3), world, Material.STEP, (byte) 7, null);
				playerQueue.addEvent((x - 3), (plugin.config_lvlHeight + 1), (y + 3), world, Material.STEP, (byte) 7, null);
				playerQueue.addEvent((x + 3), (plugin.config_lvlHeight + 1), (y - 3), world, Material.STEP, (byte) 7, null);
				playerQueue.addEvent((x + 3), (plugin.config_lvlHeight + 1), (y + 3), world, Material.STEP, (byte) 7, null);
				fill((x - 2), plugin.config_lvlHeight, (y - 3), (x + 2), plugin.config_lvlHeight, (y + 3), world, Material.GLOWSTONE, (byte) 0);
				fill((x - 3), plugin.config_lvlHeight, (y - 2), (x + 3), plugin.config_lvlHeight, (y + 2), world, Material.GLOWSTONE, (byte) 0);
				fill((x - 1), plugin.config_lvlHeight, (y - 3), (x + 1), plugin.config_lvlHeight, (y + 3), world, Material.WOOD, (byte) 5);
				fill((x - 3), plugin.config_lvlHeight, (y - 1), (x + 3), plugin.config_lvlHeight, (y + 1), world, Material.WOOD, (byte) 5);
				playerQueue.addEvent((x - 1), plugin.config_lvlHeight, y, world, Material.EMERALD_BLOCK, (byte) 0, null);
				playerQueue.addEvent((x + 1), plugin.config_lvlHeight, y, world, Material.EMERALD_BLOCK, (byte) 0, null);
				playerQueue.addEvent(x, plugin.config_lvlHeight, (y - 1), world, Material.EMERALD_BLOCK, (byte) 0, null);
				playerQueue.addEvent(x, plugin.config_lvlHeight, (y + 1), world, Material.EMERALD_BLOCK, (byte) 0, null);
				playerQueue.addEvent(x, plugin.config_lvlHeight, y, world, Material.DIAMOND_BLOCK, (byte) 0, null);
			}
		}

	}

	private void createWays() {
		for (int i = -plugin.config_radius; i <= plugin.config_radius; i++) {
			for (int j = -plugin.config_radius; j <= plugin.config_radius; j++) {
				if (i < plugin.config_radius) {
					int x = j * plugin.config_jumpInterval;
					int y = i * plugin.config_jumpInterval;
					fill((x - 3), plugin.config_lvlHeight, (y + 4), (x - 3), plugin.config_lvlHeight, (y + plugin.config_plotSize + 3), world, Material.DIRT, (byte) 0);
					fill((x - 3), (plugin.config_lvlHeight + 1), (y + 4), (x - 3), (plugin.config_lvlHeight + 1), (y + plugin.config_plotSize + 3), world, Material.STEP, (byte) 7);
					fill((x + 3), plugin.config_lvlHeight, (y + 4), (x + 3), plugin.config_lvlHeight, (y + plugin.config_plotSize + 3), world, Material.DIRT, (byte) 0);
					fill((x + 3), (plugin.config_lvlHeight + 1), (y + 4), (x + 3), (plugin.config_lvlHeight + 1), (y + plugin.config_plotSize + 3), world, Material.STEP, (byte) 7);
					fill((x - 2), plugin.config_lvlHeight, (y + 4), (x - 2), plugin.config_lvlHeight, (y + plugin.config_plotSize + 3), world, Material.GLOWSTONE, (byte) 0);
					fill((x + 2), plugin.config_lvlHeight, (y + 4), (x + 2), plugin.config_lvlHeight, (y + plugin.config_plotSize + 3), world, Material.GLOWSTONE, (byte) 0);
					fill((x - 1), plugin.config_lvlHeight, (y + 4), (x + 1), plugin.config_lvlHeight, (y + plugin.config_plotSize + 3), world, Material.WOOD, (byte) 5);
				}
				if (j < plugin.config_radius) {
					int x = j * plugin.config_jumpInterval;
					int y = i * plugin.config_jumpInterval;
					fill((x + 4), plugin.config_lvlHeight, (y - 3), (x + plugin.config_plotSize + 3), plugin.config_lvlHeight, (y - 3), world, Material.DIRT, (byte) 0);
					fill((x + 4), (plugin.config_lvlHeight + 1), (y - 3), (x + plugin.config_plotSize + 3), (plugin.config_lvlHeight + 1), (y - 3), world, Material.STEP, (byte) 7);
					fill((x + 4), plugin.config_lvlHeight, (y + 3), (x + plugin.config_plotSize + 3), plugin.config_lvlHeight, (y + 3), world, Material.DIRT, (byte) 0);
					fill((x + 4), (plugin.config_lvlHeight + 1), (y + 3), (x + plugin.config_plotSize + 3), (plugin.config_lvlHeight + 1), (y + 3), world, Material.STEP, (byte) 7);
					fill((x + 4), plugin.config_lvlHeight, (y - 2), (x + plugin.config_plotSize + 3), plugin.config_lvlHeight, (y - 2), world, Material.GLOWSTONE, (byte) 0);
					fill((x + 4), plugin.config_lvlHeight, (y + 2), (x + plugin.config_plotSize + 3), plugin.config_lvlHeight, (y + 2), world, Material.GLOWSTONE, (byte) 0);
					fill((x + 4), plugin.config_lvlHeight, (y - 1), (x + plugin.config_plotSize + 3), plugin.config_lvlHeight, (y + 1), world, Material.WOOD, (byte) 5);
				}
			}
		}
	}

	private void createFieldBorder() {
		int edgeNW = (-1 * plugin.config_radius * plugin.config_jumpInterval) - 3;
		int edgeSE = (plugin.config_radius * plugin.config_jumpInterval) + 3;
		fill(edgeNW, (plugin.config_lvlHeight + 1), edgeNW, edgeSE, (plugin.config_lvlHeight + 1), edgeNW, world, Material.STEP, (byte) 7);
		fill(edgeSE, (plugin.config_lvlHeight + 1), edgeNW, edgeSE, (plugin.config_lvlHeight + 1), edgeSE, world, Material.STEP, (byte) 7);
		fill(edgeSE, (plugin.config_lvlHeight + 1), edgeSE, edgeNW, (plugin.config_lvlHeight + 1), edgeSE, world, Material.STEP, (byte) 7);
		fill(edgeNW, (plugin.config_lvlHeight + 1), edgeSE, edgeNW, (plugin.config_lvlHeight + 1), edgeNW, world, Material.STEP, (byte) 7);
	}

	private void createPlotBorder(int posX, int posY, byte id) {
		int edgeNW_X = (posX * plugin.config_jumpInterval) + 3;
		int edgeNW_Y = (posY * plugin.config_jumpInterval) + 3;
		int edgeSE_X = ((posX + 1) * plugin.config_jumpInterval) - 3;
		int edgeSE_Y = ((posY + 1) * plugin.config_jumpInterval) - 3;
		fill(edgeNW_X, (plugin.config_lvlHeight + 1), edgeNW_Y, edgeSE_X, (plugin.config_lvlHeight + 1), edgeNW_Y, world, Material.STEP, (byte) id);
		fill(edgeSE_X, (plugin.config_lvlHeight + 1), edgeNW_Y, edgeSE_X, (plugin.config_lvlHeight + 1), edgeSE_Y, world, Material.STEP, (byte) id);
		fill(edgeSE_X, (plugin.config_lvlHeight + 1), edgeSE_Y, edgeNW_X, (plugin.config_lvlHeight + 1), edgeSE_Y, world, Material.STEP, (byte) id);
		fill(edgeNW_X, (plugin.config_lvlHeight + 1), edgeSE_Y, edgeNW_X, (plugin.config_lvlHeight + 1), edgeNW_Y, world, Material.STEP, (byte) id);
		playerQueue.addEvent(edgeNW_X, (plugin.config_lvlHeight + 1), edgeNW_Y, world, Material.QUARTZ_BLOCK, (byte) 1, null);
		String owner = "-";
		String expire = "-";
		for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
			for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
				if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == posX) && (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == posY)) {
					owner = plugin.flatMePlayers.getPlayer(i).getDisplayName();
					expire = plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableExpireDate();
				}
			}
		}
		String plotId = "X: " + String.format("%d", posX) + " Y: " + String.format("%d", posY);
		String showOwner = ChatColor.DARK_BLUE + owner;
		String showExpire = ChatColor.DARK_BLUE + expire;
		String[] args = { plotId, showOwner, showExpire };
		playerQueue.addEvent(edgeNW_X, (plugin.config_lvlHeight + 1), edgeNW_Y - 1, world, Material.WALL_SIGN, (byte) 0, args);
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
					playerQueue.addEvent(a, b, c, world, material, data, null);
				}
			}
		}
	}

}
