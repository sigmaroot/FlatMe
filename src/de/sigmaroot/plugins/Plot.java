package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;

public class Plot {

	private int id;
	private int placeX;
	private int placeY;
	private Coordinates coords;
	private Player owner;
	private List<Player> members;

	public Plot() {
		super();
		this.members = new ArrayList<Player>();
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public List<Player> getMembers() {
		return members;
	}

	public void setMembers(List<Player> members) {
		this.members = members;
	}

	public Coordinates getCoords() {
		return coords;
	}

	public void setCoords(Coordinates coords) {
		this.coords = coords;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPlaceX() {
		return placeX;
	}

	public void setPlaceX(int placeX) {
		this.placeX = placeX;
	}

	public int getPlaceY() {
		return placeY;
	}

	public void setPlaceY(int placeY) {
		this.placeY = placeY;
	}

}
