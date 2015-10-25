package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerMap {

	private List<UUID> uuids;
	private List<FlatMePlayer> players;

	public PlayerMap() {
		super();
		uuids = new ArrayList<UUID>();
		players = new ArrayList<FlatMePlayer>();
	}

	public List<UUID> getUuids() {
		return uuids;
	}

	public void setUuids(List<UUID> uuids) {
		this.uuids = uuids;
	}

	public List<FlatMePlayer> getPlayers() {
		return players;
	}

	public void setPlayers(List<FlatMePlayer> players) {
		this.players = players;
	}

	public int size() {
		return uuids.size();
	}

	public void add(UUID uuid, FlatMePlayer player) {
		uuids.add(uuid);
		players.add(player);
	}

	public void remove(int index) {
		uuids.remove(index);
		players.remove(index);
	}

	public void remove(String playerUUID) {
		for (int i = 0; i < uuids.size(); i++) {
			if (uuids.get(i).equals(playerUUID)) {
				uuids.remove(i);
				players.remove(i);
			}
		}
	}

	public void clear() {
		uuids.clear();
		players.clear();
	}

	public FlatMePlayer getPlayer(int index) {
		return players.get(index);
	}

	public FlatMePlayer getPlayer(UUID uuid) {
		for (int i = 0; i < uuids.size(); i++) {
			if (uuids.get(i).equals(uuid)) {
				return players.get(i);
			}
		}
		return null;
	}

	public UUID getUuid(int index) {
		return uuids.get(index);
	}

}
