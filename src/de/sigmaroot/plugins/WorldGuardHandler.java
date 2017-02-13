package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.World;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardHandler {

	private FlatMe plugin;
	private WorldGuardPlugin wgAPI;

	public WorldGuardHandler(FlatMe plugin, WorldGuardPlugin wgAPI) {
		super();
		this.plugin = plugin;
		this.wgAPI = wgAPI;
	}

	public WorldGuardPlugin getWgAPI() {
		return wgAPI;
	}

	public void setWgAPI(WorldGuardPlugin wgAPI) {
		this.wgAPI = wgAPI;
	}

	public int removeAllRegions(World world) {
		int removedRegions = 0;
		RegionManager wgRm = wgAPI.getRegionManager(world);
		Map<String, ProtectedRegion> regions = wgRm.getRegions();
		if ((regions != null) && (regions.isEmpty() == false)) {
			List<ProtectedRegion> regionsToDo = new ArrayList<ProtectedRegion>(regions.values());
			for (ProtectedRegion region : regionsToDo) {
				if (region.getId().contains("flatme_")) {
					wgRm.removeRegion(region.getId());
					removedRegions++;
				}
			}
		}
		return removedRegions;
	}

	public int createAllRegions(World world, PlayerQueue queue) {
		int createdRegions = 0;
		for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
			for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
				queue.addTask(plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX(), plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY(), world, QueueTaskType.CREATE_REGION);
				createdRegions++;
			}
		}
		return createdRegions;
	}

}
