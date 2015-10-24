package de.sigmaroot.plugins;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class FlatMe extends JavaPlugin implements Listener {

	public Configurator configurator;
	public FileConfiguration config;
	public CommandHandler commandHandler;
	public PlayerMap flatMePlayers;
	public final String pluginTitle = "FlatMe";
	public final String pluginVersion = "1.0";

	@Override
	public void onEnable() {
		// Use default config if no config exists
		// Create reference to loaded configuration
		saveDefaultConfig();
		config = getConfig();
		this.getLogger().info("Configuration and its defaults loaded.");
		// Create configurator for alternative configurations
		configurator = new Configurator(this, config.getString("language", "en"));
		// Create command handler
		commandHandler = new CommandHandler(this);
		// Create empty player map for queues
		flatMePlayers = new PlayerMap();
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

	@Override
	public void saveDefaultConfig() {
		File customConfigFile = null;
		customConfigFile = new File(this.getDataFolder(), "config.yml");
		if (!customConfigFile.exists()) {
			this.getLogger().warning("Configuration file config.yml doesn't exist. Using default configuration.");
			this.saveResource("config.yml", false);
		}
	}

}