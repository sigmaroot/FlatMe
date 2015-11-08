package de.sigmaroot.plugins;

import org.bukkit.World;

public class QueueTask {

	private FlatMe plugin;
	private FlatMePlayer player;
	private QueueTaskType type;
	private int posX;
	private int posY;
	private World world;

	public QueueTask(FlatMe plugin, FlatMePlayer player, QueueTaskType type, int posX, int posY, World world) {
		super();
		this.plugin = plugin;
		this.player = player;
		this.world = world;
		this.type = type;
		this.posX = posX;
		this.posY = posY;
	}

	public FlatMePlayer getPlayer() {
		return player;
	}

	public void setPlayer(FlatMePlayer player) {
		this.player = player;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public QueueTaskType getType() {
		return type;
	}

	public void setType(QueueTaskType type) {
		this.type = type;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public boolean runTask() {
		BlockChanger blockChanger = new BlockChanger(plugin, player, world);
		switch (type) {
		case CREATE_RUNWAY:
			blockChanger.runRunway(posX, posY);
			return true;
		case CREATE_AREA_BORDER:
			blockChanger.runAreaBorder();
			return true;
		case CREATE_PLOT_BORDER:
			blockChanger.runPlotBorder(posX, posY);
			return true;
		case REGEN_PLOT:
			blockChanger.runRegen(posX, posY);
			return true;
		case WE_REGEN_PLOT:
			if (player.getQueue().getBlockedForWE() == 0) {
				player.getQueue().setBlockedForWE(200);
				blockChanger.runWEregen(posX, posY);
				return true;
			} else {
				return false;
			}
		case CLEAN_MESSAGE:
			String[] args = { String.format("%d", posX), String.format("%d", posY) };
			player.sendLocalizedString("%cleaningPlot%", args);
		case ENTITY_REMOVE:
			blockChanger.runEntityRemoval(posX, posY);
		default:
			return true;
		}
	}

}
