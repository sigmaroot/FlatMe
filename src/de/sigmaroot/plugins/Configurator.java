package de.sigmaroot.plugins;

import java.io.File;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Configurator {

	private String locale = "de";
	private File dataFolder;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public File getDataFolder() {
		return dataFolder;
	}

	public void setDataFolder(File dataFolder) {
		this.dataFolder = dataFolder;
	}

	public Configurator(File dataFolder) {
		super();
		this.dataFolder = dataFolder;
	}

	public String getLocale(String text) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(dataFolder, "text_" + locale + ".yml"));
		Set<String> allNodes = config.getKeys(false);
		String[] allNodesArray = allNodes.toArray(new String[allNodes.size()]);
		for (int i = 0; i < allNodesArray.length; i++) {
			text = text.replaceAll("%" + allNodesArray[i], config.getString(allNodesArray[i]));
		}
		return text;
	}
}
