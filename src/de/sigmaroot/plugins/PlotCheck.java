package de.sigmaroot.plugins;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class PlotCheck {

	private FlatMe plugin;
	private FlatMePlayer player;
	private int posX;
	private int posY;
	private World world;

	public PlotCheck(FlatMe plugin, FlatMePlayer player) {
		super();
		this.plugin = plugin;
		this.player = player;
		if (player != null) {
			posX = plugin.getStanding(player.getPlayer()).getStartCoordX();
			posY = plugin.getStanding(player.getPlayer()).getStartCoordY();
		} else {
			posX = 0;
			posY = 0;
		}
		world = Bukkit.getWorld(plugin.config_world);
	}

	public FlatMePlayer getPlayer() {
		return player;
	}

	public void setPlayer(FlatMePlayer player) {
		this.player = player;
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

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public boolean checkForCorrectWorld() {
		if (world == null) {
			String[] args = { plugin.config_world };
			player.sendLocalizedString("%worldNotFound%", args);
			return false;
		}
		Location loc = player.getPlayer().getLocation();
		if (loc.getWorld() != world) {
			String[] args = { plugin.config_world };
			player.sendLocalizedString("%wrongWorld%", args);
			return false;
		}
		return true;
	}

	public boolean simpleCheckForCorrectWorld() {
		if (world == null) {
			return false;
		}
		return true;
	}

	public boolean checkForPlotInArea() {
		posX = plugin.getStanding(player.getPlayer()).getStartCoordX();
		posY = plugin.getStanding(player.getPlayer()).getStartCoordY();
		if (posX < 0) {
			if (Math.abs(posX) > plugin.config_radius) {
				player.sendLocalizedString("%outOfArea%", null);
				return false;
			}
		} else {
			if (posX >= plugin.config_radius) {
				player.sendLocalizedString("%outOfArea%", null);
				return false;
			}
		}
		if (posY < 0) {
			if (Math.abs(posY) > plugin.config_radius) {
				player.sendLocalizedString("%outOfArea%", null);
				return false;
			}
		} else {
			if (posY >= plugin.config_radius) {
				player.sendLocalizedString("%outOfArea%", null);
				return false;
			}
		}
		return true;
	}

	public boolean checkForPlotInArea(int posX, int posY) {
		if (posX < 0) {
			if (Math.abs(posX) > plugin.config_radius) {
				return false;
			}
		} else {
			if (posX >= plugin.config_radius) {
				return false;
			}
		}
		if (posY < 0) {
			if (Math.abs(posY) > plugin.config_radius) {
				return false;
			}
		} else {
			if (posY >= plugin.config_radius) {
				return false;
			}
		}
		return true;
	}

	public boolean checkForNextPlot() {
		Coordinates tempCoords = plugin.nextEmptyPlot();
		if (tempCoords == null) {
			player.sendLocalizedString("%noFreePlot%", null);
			return false;
		}
		posX = plugin.nextEmptyPlot().getStartCoordX();
		posY = plugin.nextEmptyPlot().getStartCoordY();
		return true;
	}

	public boolean checkForFreePlot() {
		if (!plugin.isFreePlot(posX, posY)) {
			player.sendLocalizedString("%plotAlreadyOwned%", null);
			return false;
		}
		return true;
	}

	public boolean checkForFreePlot(int posX, int posY) {
		if (!plugin.isFreePlot(posX, posY)) {
			return false;
		}
		return true;
	}

	public boolean checkForNotFreePlot() {
		if (plugin.isFreePlot(posX, posY)) {
			player.sendLocalizedString("%plotNotOwned%", null);
			return false;
		}
		return true;
	}

	public boolean checkForRightOwner() {
		if (player.getPlayer().hasPermission("flatme.admin")) {
			return true;
		}
		for (int i = 0; i < player.getPlots().size(); i++) {
			if ((player.getPlots().get(i).getPlaceX() == posX) && (player.getPlots().get(i).getPlaceY() == posY)) {
				return true;
			}
		}
		player.sendLocalizedString("%plotNotYourPlot%", null);
		return false;
	}

}
