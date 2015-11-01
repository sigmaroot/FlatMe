package de.sigmaroot.plugins;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.IEssentials;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class FlatMe extends JavaPlugin implements Listener {

	public CommandHandler commandHandler;
	public Configurator configurator;
	public PlayerMap flatMePlayers;
	public FileConfiguration config;
	public WorldGuardHandler worldGuardHandler;

	public IEssentials essAPI;
	public WorldGuardPlugin wgAPI;

	public final String PLUGIN_TITLE = "FlatMe";
	public final String PLUGIN_VERSION = "1.2";

	public int config_plotSize;
	public int config_lvlHeight;
	public int config_jumpInterval;
	public int config_radius;
	public int config_maxPlots;
	public int config_daysPerPlot;
	public int config_extendCost;
	public String config_world;

	@Override
	public void onEnable() {
		// Use default config if no config exists
		// Create reference to loaded configuration
		saveDefaultConfig();
		config = getConfig();
		this.getLogger().info("Configuration and its defaults loaded.");
		loadConfigValues();
		// Create configurator for alternative configurations
		configurator = new Configurator(this, config.getString("language", "en"));
		// Configurator: Localization
		configurator.loadLocalizationFile();
		String args_0[] = { configurator.getLocalization(), configurator.resolveLocalizedString("%language%", null) };
		this.getLogger().info(configurator.resolveLocalizedString("%localizationLoaded%", args_0));
		// Create command handler
		commandHandler = new CommandHandler(this);
		// EXTERNAL: Essentials
		essAPI = (IEssentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
		// EXTERNAL: WorldGuard
		wgAPI = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		// TRY HOOK
		this.getServer().getPluginManager().registerEvents(new OurServerListener(), this);
		if (essAPI.isEnabled() && wgAPI.isEnabled()) {
			activateHooks();
		}
		// Create empty player map for queues
		flatMePlayers = new PlayerMap(this);
		// Configurator: Plots
		configurator.loadPlotFile();
		int plotCount = configurator.loadAllPlots();
		String args_1[] = { "plots.yml", String.format("%d", plotCount) };
		this.getLogger().info(configurator.resolveLocalizedString("%plotsLoaded%", args_1));
		// Finished
		getServer().getPluginManager().registerEvents(this, this);
		this.getLogger().info(configurator.resolveLocalizedString("%pluginLoaded%", null));
	}

	@Override
	public void onDisable() {
		// Finished
		this.getLogger().info(configurator.resolveLocalizedString("%pluginUnloaded%", null));
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("flatme")) {
			if (args.length == 0) {
				sender.sendMessage(configurator.resolveLocalizedString("%noCommand%", null));
				sender.sendMessage(commandHandler.returnCorrectUsage("help"));
			} else {
				commandHandler.handleCommand(sender, args);
			}
		}
		return true;
	}

	public boolean securityCheck(FlatMePlayer player, String[] args) {
		if (player.getSecurityCommand() == null) {
			String[] warning = { args[0] };
			for (int i = 1; i < args.length; i++) {
				warning[0] = warning[0] + " " + args[i];
			}
			player.sendLocalizedString("%securityQuestion%", warning);
			player.setSecurityCommand(args);
			return false;
		} else {
			player.setSecurityCommand(null);
			return true;
		}
	}

	@Override
	public void saveDefaultConfig() {
		File customConfigFile = null;
		customConfigFile = new File(this.getDataFolder(), "config.yml");
		if (!customConfigFile.exists()) {
			this.getLogger().warning("Configuration file config.yml doesn't exist. Using default configuration.");
			this.saveResource("config.yml", false);
		}
	}

	public void loadConfigValues() {
		config_plotSize = config.getInt("plotSize", 50);
		config_lvlHeight = config.getInt("levelHeight", 3);
		config_jumpInterval = config_plotSize + 7;
		config_radius = config.getInt("radius", 5);
		config_world = config.getString("world", "world");
		config_maxPlots = config.getInt("plotsPerUser", 1);
		config_extendCost = config.getInt("extendCost", 1000);
		config_daysPerPlot = config.getInt("daysPerPlot", 60);
	}

	public void reloadConfiguration() {
		onDisable();
		flatMePlayers.stopAllQueues();
		flatMePlayers.clear();
		config = null;
		configurator = null;
		commandHandler = null;
		worldGuardHandler = null;
		essAPI = null;
		wgAPI = null;
		this.reloadConfig();
		onEnable();
	}

	public boolean isFreePlot(int placeX, int placeY) {
		for (int i = 0; i < flatMePlayers.size(); i++) {
			for (int j = 0; j < flatMePlayers.getPlayer(i).getPlots().size(); j++) {
				if ((flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == placeX) && (flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == placeY)) {
					return false;
				}
			}
		}
		return true;
	}

	public Coordinates nextEmptyPlot() {
		Coordinates tempCoords = null;
		Double bestDiff = 10000D;
		for (int i = -config_radius; i < config_radius; i++) {
			for (int j = -config_radius; j < config_radius; j++) {
				if (isFreePlot(i, j)) {
					Double centerBlockX = (i * config_jumpInterval) + (config_jumpInterval / 2D);
					Double centerBlockY = (j * config_jumpInterval) + (config_jumpInterval / 2D);
					Double thisDiff = Math.sqrt((centerBlockX * centerBlockX) + (centerBlockY * centerBlockY));
					if (thisDiff <= bestDiff) {
						if (tempCoords == null) {
							tempCoords = new Coordinates();
						}
						bestDiff = thisDiff;
						tempCoords.setStartCoordX(i);
						tempCoords.setStartCoordY(j);
					}
				}
			}
		}
		return tempCoords;
	}

	public Coordinates getStanding(Player player) {
		Location loc = player.getLocation();
		Coordinates tempCoords = new Coordinates();
		double valueX = loc.getX() / config_jumpInterval;
		double valueY = loc.getZ() / config_jumpInterval;
		tempCoords.setStartCoordX((int) Math.floor(valueX));
		tempCoords.setStartCoordY((int) Math.floor(valueY));
		return tempCoords;
	}

	private class OurServerListener implements Listener {
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			Plugin p = event.getPlugin();
			String name = p.getDescription().getName();
			if (name.equals("WorldGuard") || name.equals("Essentials")) {
				if (wgAPI.isEnabled() && essAPI.isEnabled())
					activateHooks();
			}
		}
	}

	private void activateHooks() {
		// HOOK: Essentials
		essAPI = (IEssentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
		this.getLogger().info(configurator.resolveLocalizedString("%hookedIntoEssentials%", null));
		// HOOKL: WorldGuard
		wgAPI = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		this.getLogger().info(configurator.resolveLocalizedString("%hookedIntoWorldGuard%", null));
		worldGuardHandler = new WorldGuardHandler(this, wgAPI);
	}

}