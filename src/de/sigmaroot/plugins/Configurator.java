package de.sigmaroot.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

	public void loadLocalizationFile() {
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

	public void loadPlotFile() {
		saveDefaultPlotsConfig();
		plotsFile = new File(plugin.getDataFolder(), "plots.yml");
		plotsFileConfiguration = new YamlConfiguration();
		try {
			plotsFileConfiguration.load(plotsFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int loadAllPlots() {
		int plotsLoaded = 0;
		Set<String> allNodes = plotsFileConfiguration.getKeys(false);
		String[] allNodesArray = allNodes.toArray(new String[allNodes.size()]);
		for (int i = 0; i < allNodesArray.length; i++) {
			String uuidString = allNodesArray[i];
			UUID uuid = UUID.fromString(uuidString);
			plugin.flatMePlayers.add(uuid);
			FlatMePlayer thisPlayer = plugin.flatMePlayers.getPlayer(uuid);
			int plotCount = plotsFileConfiguration.getInt(uuidString + ".plotcount", 0);
			for (int j = 1; j <= plotCount; j++) {
				Plot tempPlot = loadPlot(uuid, j);
				if (tempPlot != null) {
					thisPlayer.getPlots().add(tempPlot);
					plotsLoaded++;
				}
			}
		}
		return plotsLoaded;
	}

	public Plot loadPlot(UUID uuid, int plotNumber) {
		String uuidString = uuid.toString();
		plugin.flatMePlayers.add(uuid);
		FlatMePlayer thisPlayer = plugin.flatMePlayers.getPlayer(uuid);
		String plotAddress = uuidString + ".plots.plot" + String.format("%d", plotNumber);
		int placeX = plotsFileConfiguration.getInt(plotAddress + ".plotX", 0);
		int placeY = plotsFileConfiguration.getInt(plotAddress + ".plotY", 0);
		Long expire = plotsFileConfiguration.getLong(plotAddress + ".expire", 0);
		boolean locked = plotsFileConfiguration.getBoolean(plotAddress + ".locked", false);
		List<String> membersStringList = plotsFileConfiguration.getStringList(plotAddress + ".members");
		PlayerMap membersList = new PlayerMap(plugin);
		for (int i = 0; i < membersStringList.size(); i++) {
			UUID tempUuid = UUID.fromString(membersStringList.get(i));
			membersList.add(tempUuid);
		}
		Plot tempPlot = null;
		PlotCheck plotCheck = new PlotCheck(plugin, null);
		if (plotCheck.checkForFreePlot(placeX, placeY)) {
			if (plotCheck.checkForPlotInArea(placeX, placeY)) {
				tempPlot = new Plot(plugin, placeX, placeY, thisPlayer, membersList, expire, locked);
			}
		}
		return tempPlot;
	}

	public void saveAllPlots() {
		for (String key : plotsFileConfiguration.getKeys(false)) {
			plotsFileConfiguration.set(key, null);
		}
		for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
			plotsFileConfiguration.set(plugin.flatMePlayers.getPlayer(i).getUuid().toString() + ".plotcount", plugin.flatMePlayers.getPlayer(i).getPlots().size());
			for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
				plotsFileConfiguration.set(plugin.flatMePlayers.getPlayer(i).getUuid().toString() + ".plots.plot" + String.format("%d", (j + 1)) + ".plotX", plugin.flatMePlayers.getPlayer(i)
						.getPlots().get(j).getPlaceX());
				plotsFileConfiguration.set(plugin.flatMePlayers.getPlayer(i).getUuid().toString() + ".plots.plot" + String.format("%d", (j + 1)) + ".plotY", plugin.flatMePlayers.getPlayer(i)
						.getPlots().get(j).getPlaceY());
				List<String> allMembers = new ArrayList<String>();
				for (int k = 0; k < plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getMembers().size(); k++) {
					allMembers.add(plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getMembers().getUuid(k).toString());
				}
				plotsFileConfiguration.set(plugin.flatMePlayers.getPlayer(i).getUuid().toString() + ".plots.plot" + String.format("%d", (j + 1)) + ".members", allMembers);
				plotsFileConfiguration.set(plugin.flatMePlayers.getPlayer(i).getUuid().toString() + ".plots.plot" + String.format("%d", (j + 1)) + ".expire", plugin.flatMePlayers.getPlayer(i)
						.getPlots().get(j).getExpireDate());
				plotsFileConfiguration.set(plugin.flatMePlayers.getPlayer(i).getUuid().toString() + ".plots.plot" + String.format("%d", (j + 1)) + ".locked", plugin.flatMePlayers.getPlayer(i)
						.getPlots().get(j).isLocked());
			}
		}
		try {
			plotsFileConfiguration.save(plotsFile);
		} catch (Exception e) {
			e.printStackTrace();
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

	private void saveDefaultPlotsConfig() {
		File customConfigFile = null;
		customConfigFile = new File(plugin.getDataFolder(), "plots.yml");
		if (!customConfigFile.exists()) {
			plugin.getLogger().warning("Plots file plots.yml doesn't exist. Using default plots.");
			plugin.saveResource("plots.yml", false);
		}
	}

}
