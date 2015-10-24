package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;

public class PlayerMap {

	private List<String> playerUUIDs;
	private List<FlatMePlayer> players;

	public PlayerMap() {
		super();
		playerUUIDs = new ArrayList<String>();
		players = new ArrayList<FlatMePlayer>();
	}

	public List<String> getPlayerUUIDs() {
		return playerUUIDs;
	}

	public void setPlayerUUIDs(List<String> playerUUIDs) {
		this.playerUUIDs = playerUUIDs;
	}

	public List<FlatMePlayer> getPlayers() {
		return players;
	}

	public void setPlayers(List<FlatMePlayer> players) {
		this.players = players;
	}

	public int size() {
		return playerUUIDs.size();
	}

	public void add(String playerUUID, FlatMePlayer player) {
		playerUUIDs.add(playerUUID);
		players.add(player);
	}

	public void remove(int index) {
		playerUUIDs.remove(index);
		players.remove(index);
	}

	public void remove(String playerUUID) {
		for (int i = 0; i < playerUUIDs.size(); i++) {
			if (playerUUIDs.get(i).equals(playerUUID)) {
				playerUUIDs.remove(i);
				players.remove(i);
			}
		}
	}

	public void clear() {
		playerUUIDs.clear();
		players.clear();
	}

	public FlatMePlayer getPlayer(int index) {
		return players.get(index);
	}

	public FlatMePlayer getPlayer(String playerUUID) {
		for (int i = 0; i < playerUUIDs.size(); i++) {
			if (playerUUIDs.get(i).equals(playerUUID)) {
				return players.get(i);
			}
		}
		return null;
	}

	public String getPlayerUUID(int index) {
		return playerUUIDs.get(index);
	}

}
