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
	public final String pluginTitle = "FlatMe";
	public final String pluginVersion = "1.0";	

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("flatme")) {
			if (args.length == 0) {
				sender.sendMessage(configurator.resolveLocaledString("%noCommand%", null));
				sender.sendMessage(commandHandler.returnCorrectUsage("help"));
			} else {
				commandHandler.handleCommand(sender, args);
			}
		}
		return true;
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		config = getConfig();
		this.getLogger().info("Configuration and its defaults loaded.");
		configurator = new Configurator(this);
		configurator.setLocalization(config.getString("language"));
		this.getLogger().info(configurator.resolveLocaledString("%pluginLoaded%", null));
		getServer().getPluginManager().registerEvents(this, this);
		commandHandler = new CommandHandler(this);
	}

	@Override
	public void onDisable() {
		this.getLogger().info(configurator.resolveLocaledString("%pluginUnloaded%", null));
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