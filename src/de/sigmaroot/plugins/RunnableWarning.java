package de.sigmaroot.plugins;

import org.bukkit.Bukkit;

public class RunnableWarning {

	private FlatMe plugin;
	private FlatMePlayer player;
	private boolean onlyWillExpire;
	private String[] args;

	public RunnableWarning(FlatMe plugin, FlatMePlayer player, boolean onlyWillExpire, String[] args) {
		super();
		this.plugin = plugin;
		this.player = player;
		this.onlyWillExpire = onlyWillExpire;
		this.args = args;
	}

	public void run() {
		if (onlyWillExpire) {
			Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
				public void run() {
					runWarningWillExpire();
				}
			}, 100L);
		} else {
			Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
				public void run() {
					runWarningIsExpired();
				}
			}, 100L);
		}
	}

	public void runWarningWillExpire() {
		player.sendLocalizedString("%warningWillExpire%", args);
	}

	public void runWarningIsExpired() {
		player.sendLocalizedString("%warningExpired%", args);
	}

}
