package de.sigmaroot.plugins;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class FlatMe extends JavaPlugin implements Listener {

	public Configurator config;

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("flatme")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.DARK_RED + "Dieser Befehl muss von einem Spieler eingegeben werden!");
				return true;
			}
		}
		return true;
	}

	@Override
	public void onEnable() {
		File configPath = this.getDataFolder();
		config = new Configurator(configPath);
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
	}

	private void showTable(CommandSender sender, String title, int page, List<String> entrys) {
		int show_entrys_per_page = 5;
		int perPage = show_entrys_per_page;
		int pages = (int) Math.ceil(((double) entrys.size()) / ((double) perPage));
		if (pages < 1) {
			pages = 1;
		}
		if (page > pages) {
			page = pages;
		}
		int min = (page - 1) * perPage;
		int max = min + (perPage - 1);
		if (entrys.size() == 0) {
			entrys.add(ChatColor.DARK_RED + "Keine Einträge gefunden!");
		}
		if ((max + 1) > entrys.size()) {
			max = (entrys.size() - 1);
		}
		sender.sendMessage(ChatColor.GOLD + title);
		sender.sendMessage("=============== [Seite " + page + " von " + pages + "] ===============");
		for (int i = min; i <= max; i++) {
			sender.sendMessage(entrys.get(i));
		}
	}

	private void executeConsole(String cmd) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
	}

	private static int myRandom(int low, int high) {
		return (int) (Math.random() * (high - low) + low);
	}

	private int parsePageNumber(String test) {
		int value;
		try {
			value = Integer.parseInt(test);
		} catch (Exception e) {
			return -1;
		}
		return value;
	}

}