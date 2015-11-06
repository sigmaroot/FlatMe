package de.sigmaroot.plugins;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.World;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.profile.Profile;

public class Plot {

	private FlatMe plugin;
	private int placeX;
	private int placeY;
	private Coordinates coords;
	private FlatMePlayer owner;
	private PlayerMap members;
	private Long expireDate;
	private boolean locked;

	public Plot(FlatMe plugin, int placeX, int placeY, FlatMePlayer owner, PlayerMap members, Long expireDate, boolean locked) {
		super();
		this.plugin = plugin;
		this.placeX = placeX;
		this.placeY = placeY;
		this.owner = owner;
		this.members = members;
		this.expireDate = expireDate;
		this.locked = locked;
		coords = calculateCoords();
	}

	public FlatMePlayer getOwner() {
		return owner;
	}

	public void setOwner(FlatMePlayer owner) {
		this.owner = owner;
	}

	public PlayerMap getMembers() {
		return members;
	}

	public void setMembers(PlayerMap members) {
		this.members = members;
	}

	public Coordinates getCoords() {
		return coords;
	}

	public void setCoords(Coordinates coords) {
		this.coords = coords;
	}

	public int getPlaceX() {
		return placeX;
	}

	public void setPlaceX(int placeX) {
		this.placeX = placeX;
		coords = calculateCoords();
	}

	public int getPlaceY() {
		return placeY;
	}

	public void setPlaceY(int placeY) {
		this.placeY = placeY;
		coords = calculateCoords();
	}

	public Long getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Long expireDate) {
		this.expireDate = expireDate;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isExpired() {
		Long now = System.currentTimeMillis();
		if (expireDate < now) {
			return true;
		}
		return false;
	}

	public boolean willExpire() {
		Long now = System.currentTimeMillis() + 604800000L;
		if (expireDate < now) {
			return true;
		}
		return false;
	}

	public void toggleLocked() {
		if (locked) {
			locked = false;
		} else {
			locked = true;
		}
	}

	public String getReadableMemberList() {
		if (members.size() > 0) {
			String memberList = "";
			for (int i = 0; i < members.size(); i++) {
				if (memberList.equals("")) {
					memberList = memberList + members.getPlayer(i).getDisplayName();
				} else {
					memberList = memberList + ", " + members.getPlayer(i).getDisplayName();
				}
			}
			return memberList;
		} else {
			return plugin.configurator.resolveLocalizedString("%noMembers%", null);
		}
	}

	public void createWGRegion(World world) {
		RegionManager wgRm = plugin.wgAPI.getRegionManager(world);
		BlockVector startBlock = new BlockVector(coords.getStartCoordX(), 0, coords.getStartCoordY());
		BlockVector endBlock = new BlockVector(coords.getEndCoordX(), (world.getMaxHeight() - 1), coords.getEndCoordY());
		ProtectedRegion newRegion = new ProtectedCuboidRegion("flatme_" + String.format("%d", placeX) + "_" + String.format("%d", placeY), startBlock, endBlock);
		DefaultDomain newOwners = new DefaultDomain();
		plugin.wgAPI.getProfileCache().put(new Profile(owner.getUuid(), owner.getDisplayName()));
		newOwners.addPlayer(owner.getUuid());
		newRegion.setOwners(newOwners);
		DefaultDomain newMembers = new DefaultDomain();
		for (int i = 0; i < members.size(); i++) {
			plugin.wgAPI.getProfileCache().put(new Profile(members.getPlayer(i).getUuid(), members.getPlayer(i).getDisplayName()));
			newMembers.addPlayer(members.getPlayer(i).getUuid());
		}
		newRegion.setMembers(newMembers);
		// newRegion.setFlag(DefaultFlag.USE, StateFlag.State.ALLOW);
		wgRm.addRegion(newRegion);
	}

	public void deleteWGRegion(World world) {
		RegionManager wgRm = plugin.wgAPI.getRegionManager(world);
		wgRm.removeRegion("flatme_" + String.format("%d", placeX) + "_" + String.format("%d", placeY));
	}

	public String getReadableExpireDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
		Date resultdate = new Date(expireDate);
		return sdf.format(resultdate);
	}

	private Coordinates calculateCoords() {
		int plotSize = plugin.config.getInt("plotSize", 50);
		int jumpInterval = plotSize + 7;
		Coordinates coords = new Coordinates();
		coords.setStartCoordX((placeX * jumpInterval) + 4);
		coords.setStartCoordY((placeY * jumpInterval) + 4);
		coords.setEndCoordX(coords.getStartCoordX() + plotSize - 1);
		coords.setEndCoordY(coords.getStartCoordY() + plotSize - 1);
		return coords;
	}

}
