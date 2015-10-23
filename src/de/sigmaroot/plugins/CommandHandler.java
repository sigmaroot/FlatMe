package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandHandler {

	private FlatMe plugin;
	private CommandMap commandList;

	public CommandMap getCommandList() {
		return commandList;
	}

	public void setCommandList(CommandMap commandList) {
		this.commandList = commandList;
	}

	public CommandHandler(FlatMe plugin) {
		super();
		this.plugin = plugin;
		commandList = new CommandMap();
		initializeAllCommands();
	}

	private void initializeAllCommands() {
		commandList.add("help", new Command("flatme.player", "/flatme help [page]", 0));
		commandList.add("version", new Command("flatme.player", "/flatme version", 0));
	}

	public void handleCommand(CommandSender sender, String[] args) {
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].toLowerCase();
		}
		Command executedCommand;
		String firstArg = args[0];
		try {
			executedCommand = commandList.getCommand(firstArg);
		} catch (Exception e) {
			String[] args_0 = { firstArg };
			sendLocalizedString(sender, "%commandError%", args_0);
			sendLocalizedString(sender, returnCorrectUsage("help"), null);
			return;
		}
		if (executedCommand == null) {
			String[] args_0 = { firstArg };
			sendLocalizedString(sender, "%commandNotFound%", args_0);
			sendLocalizedString(sender, returnCorrectUsage("help"), null);
			return;
		}
		if (!executedCommand.enoughArguments(args)) {
			sendLocalizedString(sender, returnCorrectUsage(firstArg), null);
			return;
		}
		switch (firstArg) {
		case "help":
			int page = 1;
			if (args.length > 1) {
				page = parsePageNumber(args[1]);
			}
			showAllCommands(sender, page);
			break;
		case "version":
			String[] args_0 = { plugin.pluginVersion };
			sendLocalizedString(sender, "%pluginVersion%", args_0);
			break;
		default:
			String[] args_1 = { firstArg };
			sendLocalizedString(sender, "%commandError%", args_1);
			sendLocalizedString(sender, returnCorrectUsage("help"), null);
		}
	}

	public String returnCorrectUsage(String command) {
		String[] args_0 = { commandList.getCommand("help").getUsage() };
		return plugin.configurator.resolveLocaledString("%correctUsage%", args_0);
	}

	public void sendLocalizedString(CommandSender sender, String input, String[] args) {
		sender.sendMessage(plugin.configurator.resolveLocaledString(input, args));
	}

	private void showAllCommands(CommandSender sender, int page) {
		List<String> temp = new ArrayList<String>();
		plugin.getLogger().info(Integer.toString(commandList.size()));
		for (int i = 0; i < commandList.size(); i++) {
			String[] args_0 = { commandList.getCommand(i).getUsage() };
			temp.add(plugin.configurator.resolveLocaledString("%cmd_" + commandList.getCommandText(i) + "%", args_0));
		}
		String[] args_0 = { plugin.pluginTitle, plugin.pluginVersion };
		showTable(sender, plugin.configurator.resolveLocaledString("%pluginTitle% &6- Plugin by Enatras", args_0), page, temp);
	}

	private void showTable(CommandSender sender, String title, int page, List<String> entrys) {
		int show_entrys_per_page = 5;
		int perPage = show_entrys_per_page;
		int pages = (int) Math.ceil(((double) entrys.size()) / ((double) perPage));
		if (pages < 1) {
			pages = 1;
		}
		if (page < 1) {
			page = 1;
		}
		if (page > pages) {
			page = pages;
		}
		int min = (page - 1) * perPage;
		int max = min + (perPage - 1);
		if (entrys.size() == 0) {
			entrys.add(plugin.configurator.resolveLocaledString("%noResultsFound%", null));
		}
		if ((max + 1) > entrys.size()) {
			max = (entrys.size() - 1);
		}
		sender.sendMessage(plugin.configurator.resolveLocaledString(title, null));
		String args_0[] = { Integer.toString(page), Integer.toString(pages) };
		sender.sendMessage(plugin.configurator.resolveLocaledString("%pageTitle%", args_0));
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
			return 1;
		}
		return value;
	}

}
