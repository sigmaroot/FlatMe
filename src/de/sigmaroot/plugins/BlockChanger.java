package de.sigmaroot.plugins;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

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

	public BlockChanger(FlatMe plugin, World world) {
		super();
		this.plugin = plugin;
		this.world = world;
		this.player = null;
		playerQueue = null;
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

	public void runRunway(int posX, int posY) {
		createCenterPoint(posX, posY);
		createWays(posX, posY);
	}

	public void runAreaBorder() {
		createAreaBorder();
	}

	public void runPlotBorder(int posX, int posY) {
		createPlotBorder(posX, posY);
	}

	public void runRegen(int posX, int posY) {
		fill(((posX * plugin.config_jumpInterval) + 4), 1, ((posY * plugin.config_jumpInterval) + 4), (((posX + 1) * plugin.config_jumpInterval) - 4), (plugin.config_levelHeight - 1),
				(((posY + 1) * plugin.config_jumpInterval) - 4), world, Material.DIRT, (byte) 0);
		fill(((posX * plugin.config_jumpInterval) + 4), plugin.config_levelHeight, ((posY * plugin.config_jumpInterval) + 4), (((posX + 1) * plugin.config_jumpInterval) - 4),
				plugin.config_levelHeight, (((posY + 1) * plugin.config_jumpInterval) - 4), world, Material.GRASS, (byte) 0);
		fill(((posX * plugin.config_jumpInterval) + 4), (plugin.config_levelHeight + 1), ((posY * plugin.config_jumpInterval) + 4), (((posX + 1) * plugin.config_jumpInterval) - 4),
				(world.getMaxHeight() - 1), (((posY + 1) * plugin.config_jumpInterval) - 4), world, Material.AIR, (byte) 0);
	}

	public void runWEregen(int posX, int posY) {
		player.getPlayer().performCommand("/pos1 " + ((posX * plugin.config_jumpInterval) + 4) + ",0," + ((posY * plugin.config_jumpInterval) + 4));
		player.getPlayer().performCommand("/pos2 " + (((posX + 1) * plugin.config_jumpInterval) - 4) + "," + (world.getMaxHeight() - 1) + "," + (((posY + 1) * plugin.config_jumpInterval) - 4));
		player.getPlayer().performCommand("/regen");
	}

	public void runWEcopy(int posX, int posY) {
		player.getPlayer().performCommand("/pos1 " + ((posX * plugin.config_jumpInterval) + 4) + ",0," + ((posY * plugin.config_jumpInterval) + 4));
		player.getPlayer().performCommand("/pos2 " + (((posX + 1) * plugin.config_jumpInterval) - 4) + "," + (world.getMaxHeight() - 1) + "," + (((posY + 1) * plugin.config_jumpInterval) - 4));
		player.getPlayer().performCommand("/copy");
	}

	public void runWEpaste(int posX, int posY) {
		player.getPlayer().performCommand("/pos1 " + ((posX * plugin.config_jumpInterval) + 4) + ",0," + ((posY * plugin.config_jumpInterval) + 4));
		player.getPlayer().performCommand("/pos2 " + (((posX + 1) * plugin.config_jumpInterval) - 4) + "," + (world.getMaxHeight() - 1) + "," + (((posY + 1) * plugin.config_jumpInterval) - 4));
		player.getPlayer().performCommand("/paste");
	}

	public void runEntityRemoval(int posX, int posY) {
		removeEntities(posX, posY);
	}

	private void createCenterPoint(int posX, int posY) {
		playerQueue.stopQueue();
		int x = posX * plugin.config_jumpInterval;
		int y = posY * plugin.config_jumpInterval;
		fill((x - 3), (plugin.config_levelHeight + 1), (y - 3), (x + 3), (plugin.config_levelHeight + 6), (y + 3), world, Material.AIR, (byte) 0);
		playerQueue.addEvent((x - 3), plugin.config_levelHeight, (y - 3), world, Material.DIRT, (byte) 0, null);
		playerQueue.addEvent((x - 3), plugin.config_levelHeight, (y + 3), world, Material.DIRT, (byte) 0, null);
		playerQueue.addEvent((x + 3), plugin.config_levelHeight, (y - 3), world, Material.DIRT, (byte) 0, null);
		playerQueue.addEvent((x + 3), plugin.config_levelHeight, (y + 3), world, Material.DIRT, (byte) 0, null);
		playerQueue.addEvent((x - 3), (plugin.config_levelHeight + 1), (y - 3), world, Material.STEP, (byte) 7, null);
		playerQueue.addEvent((x - 3), (plugin.config_levelHeight + 1), (y + 3), world, Material.STEP, (byte) 7, null);
		playerQueue.addEvent((x + 3), (plugin.config_levelHeight + 1), (y - 3), world, Material.STEP, (byte) 7, null);
		playerQueue.addEvent((x + 3), (plugin.config_levelHeight + 1), (y + 3), world, Material.STEP, (byte) 7, null);
		fill((x - 2), plugin.config_levelHeight, (y - 3), (x + 2), plugin.config_levelHeight, (y + 3), world, Material.GLOWSTONE, (byte) 0);
		fill((x - 3), plugin.config_levelHeight, (y - 2), (x + 3), plugin.config_levelHeight, (y + 2), world, Material.GLOWSTONE, (byte) 0);
		fill((x - 1), plugin.config_levelHeight, (y - 3), (x + 1), plugin.config_levelHeight, (y + 3), world, Material.WOOD, (byte) 5);
		fill((x - 3), plugin.config_levelHeight, (y - 1), (x + 3), plugin.config_levelHeight, (y + 1), world, Material.WOOD, (byte) 5);
		playerQueue.addEvent((x - 1), plugin.config_levelHeight, y, world, Material.EMERALD_BLOCK, (byte) 0, null);
		playerQueue.addEvent((x + 1), plugin.config_levelHeight, y, world, Material.EMERALD_BLOCK, (byte) 0, null);
		playerQueue.addEvent(x, plugin.config_levelHeight, (y - 1), world, Material.EMERALD_BLOCK, (byte) 0, null);
		playerQueue.addEvent(x, plugin.config_levelHeight, (y + 1), world, Material.EMERALD_BLOCK, (byte) 0, null);
		playerQueue.addEvent(x, plugin.config_levelHeight, y, world, Material.DIAMOND_BLOCK, (byte) 0, null);
	}

	private void createWays(int posX, int posY) {
		if (posY < plugin.config_radius) {
			int x = posX * plugin.config_jumpInterval;
			int y = posY * plugin.config_jumpInterval;
			fill((x - 3), (plugin.config_levelHeight + 1), (y + 4), (x + 3), (plugin.config_levelHeight + 6), (y + plugin.config_plotSize + 3), world, Material.AIR, (byte) 0);
			fill((x - 3), plugin.config_levelHeight, (y + 4), (x - 3), plugin.config_levelHeight, (y + plugin.config_plotSize + 3), world, Material.DIRT, (byte) 0);
			fill((x - 3), (plugin.config_levelHeight + 1), (y + 4), (x - 3), (plugin.config_levelHeight + 1), (y + plugin.config_plotSize + 3), world, Material.STEP, (byte) 7);
			fill((x + 3), plugin.config_levelHeight, (y + 4), (x + 3), plugin.config_levelHeight, (y + plugin.config_plotSize + 3), world, Material.DIRT, (byte) 0);
			fill((x + 3), (plugin.config_levelHeight + 1), (y + 4), (x + 3), (plugin.config_levelHeight + 1), (y + plugin.config_plotSize + 3), world, Material.STEP, (byte) 7);
			fill((x - 2), plugin.config_levelHeight, (y + 4), (x - 2), plugin.config_levelHeight, (y + plugin.config_plotSize + 3), world, Material.GLOWSTONE, (byte) 0);
			fill((x + 2), plugin.config_levelHeight, (y + 4), (x + 2), plugin.config_levelHeight, (y + plugin.config_plotSize + 3), world, Material.GLOWSTONE, (byte) 0);
			fill((x - 1), plugin.config_levelHeight, (y + 4), (x + 1), plugin.config_levelHeight, (y + plugin.config_plotSize + 3), world, Material.WOOD, (byte) 5);
		}
		if (posX < plugin.config_radius) {
			int x = posX * plugin.config_jumpInterval;
			int y = posY * plugin.config_jumpInterval;
			fill((x + 4), (plugin.config_levelHeight + 1), (y - 3), (x + plugin.config_plotSize + 3), (plugin.config_levelHeight + 6), (y + 3), world, Material.AIR, (byte) 0);
			fill((x + 4), plugin.config_levelHeight, (y - 3), (x + plugin.config_plotSize + 3), plugin.config_levelHeight, (y - 3), world, Material.DIRT, (byte) 0);
			fill((x + 4), (plugin.config_levelHeight + 1), (y - 3), (x + plugin.config_plotSize + 3), (plugin.config_levelHeight + 1), (y - 3), world, Material.STEP, (byte) 7);
			fill((x + 4), plugin.config_levelHeight, (y + 3), (x + plugin.config_plotSize + 3), plugin.config_levelHeight, (y + 3), world, Material.DIRT, (byte) 0);
			fill((x + 4), (plugin.config_levelHeight + 1), (y + 3), (x + plugin.config_plotSize + 3), (plugin.config_levelHeight + 1), (y + 3), world, Material.STEP, (byte) 7);
			fill((x + 4), plugin.config_levelHeight, (y - 2), (x + plugin.config_plotSize + 3), plugin.config_levelHeight, (y - 2), world, Material.GLOWSTONE, (byte) 0);
			fill((x + 4), plugin.config_levelHeight, (y + 2), (x + plugin.config_plotSize + 3), plugin.config_levelHeight, (y + 2), world, Material.GLOWSTONE, (byte) 0);
			fill((x + 4), plugin.config_levelHeight, (y - 1), (x + plugin.config_plotSize + 3), plugin.config_levelHeight, (y + 1), world, Material.WOOD, (byte) 5);
		}
	}

	private void createAreaBorder() {
		int edgeNW = (-1 * plugin.config_radius * plugin.config_jumpInterval) - 3;
		int edgeSE = (plugin.config_radius * plugin.config_jumpInterval) + 3;
		fill(edgeNW, (plugin.config_levelHeight + 1), edgeNW, edgeSE, (plugin.config_levelHeight + 1), edgeNW, world, Material.QUARTZ_BLOCK, (byte) 0);
		fill(edgeSE, (plugin.config_levelHeight + 1), edgeNW, edgeSE, (plugin.config_levelHeight + 1), edgeSE, world, Material.QUARTZ_BLOCK, (byte) 0);
		fill(edgeSE, (plugin.config_levelHeight + 1), edgeSE, edgeNW, (plugin.config_levelHeight + 1), edgeSE, world, Material.QUARTZ_BLOCK, (byte) 0);
		fill(edgeNW, (plugin.config_levelHeight + 1), edgeSE, edgeNW, (plugin.config_levelHeight + 1), edgeNW, world, Material.QUARTZ_BLOCK, (byte) 0);
	}

	private void createPlotBorder(int posX, int posY) {
		String owner = "-";
		String expire = "-";
		boolean isFree = true;
		boolean isExpired = false;
		boolean isLocked = false;
		for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
			for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
				if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == posX) && (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == posY)) {
					owner = plugin.flatMePlayers.getPlayer(i).getDisplayName();
					expire = plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableExpireDate();
					isFree = false;
					isExpired = plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired();
					isLocked = plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isLocked();
				}
			}
		}
		byte id = (byte) 0;
		if (isFree) {
			id = (byte) 7;
		} else {
			if (!isLocked) {
				if (!isExpired) {
					id = (byte) 6;
				} else {
					id = (byte) 4;
				}
			}
		}
		int edgeNW_X = (posX * plugin.config_jumpInterval) + 3;
		int edgeNW_Y = (posY * plugin.config_jumpInterval) + 3;
		int edgeSE_X = ((posX + 1) * plugin.config_jumpInterval) - 3;
		int edgeSE_Y = ((posY + 1) * plugin.config_jumpInterval) - 3;
		fill(edgeNW_X, (plugin.config_levelHeight + 1), edgeNW_Y, edgeSE_X, (plugin.config_levelHeight + 1), edgeNW_Y, world, Material.STEP, (byte) id);
		fill(edgeSE_X, (plugin.config_levelHeight + 1), edgeNW_Y, edgeSE_X, (plugin.config_levelHeight + 1), edgeSE_Y, world, Material.STEP, (byte) id);
		fill(edgeSE_X, (plugin.config_levelHeight + 1), edgeSE_Y, edgeNW_X, (plugin.config_levelHeight + 1), edgeSE_Y, world, Material.STEP, (byte) id);
		fill(edgeNW_X, (plugin.config_levelHeight + 1), edgeSE_Y, edgeNW_X, (plugin.config_levelHeight + 1), edgeNW_Y, world, Material.STEP, (byte) id);
		playerQueue.addEvent(edgeNW_X, (plugin.config_levelHeight + 1), edgeNW_Y, world, Material.QUARTZ_BLOCK, (byte) 1, null);
		playerQueue.addEvent(edgeSE_X, (plugin.config_levelHeight + 1), edgeNW_Y, world, Material.QUARTZ_BLOCK, (byte) 1, null);
		playerQueue.addEvent(edgeNW_X, (plugin.config_levelHeight + 1), edgeSE_Y, world, Material.QUARTZ_BLOCK, (byte) 1, null);
		playerQueue.addEvent(edgeSE_X, (plugin.config_levelHeight + 1), edgeSE_Y, world, Material.QUARTZ_BLOCK, (byte) 1, null);
		String plotId = "X: " + String.format("%d", posX) + " Y: " + String.format("%d", posY);
		String showOwner = ChatColor.DARK_BLUE + owner;
		String showExpire = "";
		if (isLocked) {
			showExpire = ChatColor.DARK_PURPLE + expire;
		} else {
			if (isExpired) {
				showExpire = ChatColor.DARK_RED + expire;
			} else {
				showExpire = ChatColor.DARK_BLUE + expire;
			}
		}
		String[] args_1 = { plotId, showOwner, showExpire, "north" };
		playerQueue.addEvent(edgeNW_X, (plugin.config_levelHeight + 1), edgeNW_Y - 1, world, Material.WALL_SIGN, (byte) 0, args_1);
		String[] args_2 = { plotId, showOwner, showExpire, "west" };
		playerQueue.addEvent(edgeNW_X - 1, (plugin.config_levelHeight + 1), edgeNW_Y, world, Material.WALL_SIGN, (byte) 0, args_2);
		String[] args_3 = { plotId, showOwner, showExpire, "north" };
		playerQueue.addEvent(edgeSE_X, (plugin.config_levelHeight + 1), edgeNW_Y - 1, world, Material.WALL_SIGN, (byte) 0, args_3);
		String[] args_4 = { plotId, showOwner, showExpire, "east" };
		playerQueue.addEvent(edgeSE_X + 1, (plugin.config_levelHeight + 1), edgeNW_Y, world, Material.WALL_SIGN, (byte) 0, args_4);
		String[] args_5 = { plotId, showOwner, showExpire, "south" };
		playerQueue.addEvent(edgeNW_X, (plugin.config_levelHeight + 1), edgeSE_Y + 1, world, Material.WALL_SIGN, (byte) 0, args_5);
		String[] args_6 = { plotId, showOwner, showExpire, "west" };
		playerQueue.addEvent(edgeNW_X - 1, (plugin.config_levelHeight + 1), edgeSE_Y, world, Material.WALL_SIGN, (byte) 0, args_6);
		String[] args_7 = { plotId, showOwner, showExpire, "south" };
		playerQueue.addEvent(edgeSE_X, (plugin.config_levelHeight + 1), edgeSE_Y + 1, world, Material.WALL_SIGN, (byte) 0, args_7);
		String[] args_8 = { plotId, showOwner, showExpire, "east" };
		playerQueue.addEvent(edgeSE_X + 1, (plugin.config_levelHeight + 1), edgeSE_Y, world, Material.WALL_SIGN, (byte) 0, args_8);
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

	private void removeEntities(int posX, int posY) {
		Coordinates thisPlot = calculateCoords(posX, posY);
		thisPlot.calculateAllChunks();
		thisPlot.loadChunks(world);
		List<Entity> entList = world.getEntities();
		for (Entity current : entList) {
			if (current instanceof Item) {
				if (testForLocation(thisPlot, current)) {
					current.remove();
				}
			}
		}
	}

	private boolean testForLocation(Coordinates thisPlot, Entity entity) {
		Coordinates entityPos = new Coordinates();
		entityPos.setSimpleCoordX(entity.getLocation().getBlockX());
		entityPos.setSimpleCoordY(entity.getLocation().getBlockZ());
		boolean xOkay = false;
		boolean yOkay = false;
		if (((entityPos.getSimpleCoordX() >= thisPlot.getStartCoordX()) && (entityPos.getSimpleCoordX() <= thisPlot.getEndCoordX()))
				|| ((entityPos.getSimpleCoordX() <= thisPlot.getStartCoordX()) && (entityPos.getSimpleCoordX() >= thisPlot.getEndCoordX()))) {
			xOkay = true;
		}
		if (((entityPos.getSimpleCoordY() >= thisPlot.getStartCoordY()) && (entityPos.getSimpleCoordY() <= thisPlot.getEndCoordY()))
				|| ((entityPos.getSimpleCoordY() <= thisPlot.getStartCoordY()) && (entityPos.getSimpleCoordY() >= thisPlot.getEndCoordY()))) {
			yOkay = true;
		}
		return (xOkay && yOkay);
	}

	private Coordinates calculateCoords(int posX, int posY) {
		Coordinates coords = new Coordinates();
		coords.setStartCoordX((posX * plugin.config_jumpInterval) + 2);
		coords.setStartCoordY((posY * plugin.config_jumpInterval) + 2);
		coords.setEndCoordX(coords.getStartCoordX() + plugin.config_plotSize + 3);
		coords.setEndCoordY(coords.getStartCoordY() + plugin.config_plotSize + 3);
		return coords;
	}
}
