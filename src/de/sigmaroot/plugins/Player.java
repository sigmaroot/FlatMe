package de.sigmaroot.plugins;

import java.util.List;

import com.earth2me.essentials.User;

public class Player {

	private String uuid;
	private String displayName;
	private User essentialsUser;
	private List<Integer> plots;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public User getEssentialsUser() {
		return essentialsUser;
	}

	public void setEssentialsUser(User essentialsUser) {
		this.essentialsUser = essentialsUser;
	}

	public List<Integer> getPlots() {
		return plots;
	}

	public void setPlots(List<Integer> plots) {
		this.plots = plots;
	}

}
