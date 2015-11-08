package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerMap {

	private FlatMe plugin;
	private List<UUID> uuids;
	private List<FlatMePlayer> players;

	public PlayerMap(FlatMe plugin) {
		super();
		this.plugin = plugin;
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

	public void add(UUID uuid) {
		if (getPlayer(uuid) == null) {
			FlatMePlayer player = new FlatMePlayer(plugin, uuid);
			uuids.add(uuid);
			players.add(player);
		}
	}

	public void remove(int index) {
		uuids.remove(index);
		players.remove(index);
	}

	public void remove(UUID uuid) {
		for (int i = 0; i < uuids.size(); i++) {
			if (uuids.get(i).equals(uuid)) {
				uuids.remove(i);
				if (players.get(i).getQueue().isRunning()) {
					players.get(i).getQueue().stopQueue();
				}
				players.remove(i);
			}
		}
	}

	public void clear() {
		uuids.clear();
		stopAllQueues();
		players.clear();
	}

	public FlatMePlayer getPlayer(int index) {
		players.get(index).checkForPlayer();
		return players.get(index);
	}

	public FlatMePlayer getPlayer(UUID uuid) {
		for (int i = 0; i < uuids.size(); i++) {
			if (uuids.get(i).equals(uuid)) {
				players.get(i).checkForPlayer();
				return players.get(i);
			}
		}
		return null;
	}

	public UUID getUuid(int index) {
		return uuids.get(index);
	}

	public void stopAllQueues() {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getQueue().isRunning()) {
				players.get(i).getQueue().stopTaskQueue();
			}
		}
	}
}
