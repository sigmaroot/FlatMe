package de.sigmaroot.plugins;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
	public final String PLUGIN_VERSION = "1.8.9";

	public boolean config_autoUpdate;
	public int config_daysPerPlot;
	public int config_extendCost;
	public int config_levelHeight;
	public int config_maxBlocksPerTick;
	public int config_maxResultsPerPage;
	public int config_plotSize;
	public int config_plotsPerUser;
	public int config_portDelay;
	public int config_radius;
	public String config_world;
	public int config_jumpInterval;

	private boolean areRegistered = false;

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
		if (!areRegistered) {
			getServer().getPluginManager().registerEvents(this, this);
			areRegistered = true;
		}
		this.getLogger().info(configurator.resolveLocalizedString("%pluginLoaded%", null));
	}

	@Override
	public void onDisable() {
		flatMePlayers.stopAllQueues();
		configurator.saveAllPlots();
		configurator.backupPlotFile();
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

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		flatMePlayers.add(player.getUniqueId());
		flatMePlayers.getPlayer(player.getUniqueId()).checkForPlayer();
		flatMePlayers.getPlayer(player.getUniqueId()).checkForPlots();
		flatMePlayers.getPlayer(player.getUniqueId()).getQueue().setSilence(!player.hasPermission("flatme.admin"));
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
		config_autoUpdate = config.getBoolean("autoUpdate", true);
		config_daysPerPlot = config.getInt("daysPerPlot", 60);
		config_extendCost = config.getInt("extendCost", 1000);
		config_levelHeight = config.getInt("levelHeight", 3);
		config_maxBlocksPerTick = config.getInt("maxBlocksPerTick", 1000);
		config_maxResultsPerPage = config.getInt("maxResultsPerPage", 5);
		config_plotSize = config.getInt("plotSize", 50);
		config_plotsPerUser = config.getInt("plotsPerUser", 1);
		config_portDelay = config.getInt("portDelay", 3);
		config_radius = config.getInt("radius", 3);
		config_world = config.getString("world", "world");
		config_jumpInterval = config_plotSize + 7;
	}

	public void reloadConfiguration() {
		onDisable();
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
		if (worldGuardHandler == null) {
			worldGuardHandler = new WorldGuardHandler(this, wgAPI);
			if (config_autoUpdate) {
				this.getLogger().info(configurator.resolveLocalizedString("%autoUpdateTriggered%", null));
				Bukkit.getServer().getScheduler().runTaskLater(this, new Runnable() {
					public void run() {
						Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "flatme update");
					}
				}, 1200L);
			}
		}
	}

}