package de.sigmaroot.plugins;

import java.io.File;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Configurator {

	private FlatMe plugin;
	private String localization;
	private StringMap localizedStrings;
	private File localizationFile;
	private FileConfiguration localizationFileConfiguration;
	private File plotsFile;
	private FileConfiguration plotsFileConfiguration;

	public Configurator(FlatMe plugin, String localization) {
		super();
		this.plugin = plugin;
		this.localization = localization;
		loadLocalizationFile();
		String args_0[] = { this.localization, resolveLocalizedString("%language%", null) };
		plugin.getLogger().info(resolveLocalizedString("%localizationLoaded%", args_0));
		loadPlotFile();
		String args_1[] = { "plots.yml" };
		plugin.getLogger().info(resolveLocalizedString("%plotsLoaded%", args_1));

	}

	public StringMap getLocalizedStrings() {
		return localizedStrings;
	}

	public void setLocalizedStrings(StringMap localizedStrings) {
		this.localizedStrings = localizedStrings;
	}

	public File getLocalizationFile() {
		return localizationFile;
	}

	public void setLocalizationFile(File localizationFile) {
		this.localizationFile = localizationFile;
	}

	public FileConfiguration getLocalizationFileConfiguration() {
		return localizationFileConfiguration;
	}

	public void setLocalizationFileConfiguration(FileConfiguration localizationFileConfiguration) {
		this.localizationFileConfiguration = localizationFileConfiguration;
	}

	public File getPlotsFile() {
		return plotsFile;
	}

	public void setPlotsFile(File plotsFile) {
		this.plotsFile = plotsFile;
	}

	public FileConfiguration getPlotsFileConfiguration() {
		return plotsFileConfiguration;
	}

	public void setPlotsFileConfiguration(FileConfiguration plotsFileConfiguration) {
		this.plotsFileConfiguration = plotsFileConfiguration;
	}

	public String getLocalization() {
		return localization;
	}

	public void setLocalization(String localization) {
		this.localization = localization;
	}

	public String resolveLocalizedString(String input, String[] args) {
		for (int i = 0; i < localizedStrings.size(); i++) {
			input = input.replaceAll(localizedStrings.getIndex(i), localizedStrings.getString(i));
		}
		if (args != null) {
			for (int i = 1; i < (args.length + 1); i++) {
				input = input.replaceAll("%" + String.format("%d", i) + "%", args[i - 1]);
			}
		}
		input = ChatColor.translateAlternateColorCodes('&', input);
		return input;
	}

	private void loadLocalizationFile() {
		saveDefaultLocalizationConfig("text_" + localization + ".yml");
		localizationFile = new File(plugin.getDataFolder(), "text_" + localization + ".yml");
		localizationFileConfiguration = new YamlConfiguration();
		try {
			localizationFileConfiguration.load(localizationFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Set<String> allNodes = localizationFileConfiguration.getKeys(false);
		String[] allNodesArray = allNodes.toArray(new String[allNodes.size()]);
		localizedStrings = new StringMap();
		for (int i = 0; i < allNodesArray.length; i++) {
			localizedStrings.add("%" + allNodesArray[i] + "%", localizationFileConfiguration.getString(allNodesArray[i]));
		}
	}

	private void saveDefaultLocalizationConfig(String configName) {
		File customConfigFile = null;
		customConfigFile = new File(plugin.getDataFolder(), configName);
		if (!customConfigFile.exists()) {
			plugin.getLogger().warning("Localization file " + configName + " doesn't exist. Using default localization.");
			localization = "en";
			configName = "text_en.yml";
			plugin.saveResource(configName, false);
		}
	}

	private void loadPlotFile() {
		saveDefaultPlotsConfig();
		plotsFile = new File(plugin.getDataFolder(), "plots.yml");
		plotsFileConfiguration = new YamlConfiguration();
		try {
			plotsFileConfiguration.load(plotsFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Set<String> allNodes = localizationFileConfiguration.getKeys(false);
		// String[] allNodesArray = allNodes.toArray(new
		// String[allNodes.size()]);
		// localizedStrings = new StringMap();
		// for (int i = 0; i < allNodesArray.length; i++) {
		// localizedStrings.add("%" + allNodesArray[i] + "%",
		// localizationFileConfiguration.getString(allNodesArray[i]));
		// }
	}

	private void saveDefaultPlotsConfig() {
		File customConfigFile = null;
		customConfigFile = new File(plugin.getDataFolder(), "plots.yml");
		if (!customConfigFile.exists()) {
			plugin.getLogger().warning("Plots file plots.yml doesn't exist. Using default plots.");
			plugin.saveResource("plots.yml", false);
		}
	}

}
