package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler {

	private FlatMe plugin;
	private CommandMap commandList;

	public CommandHandler(FlatMe plugin) {
		super();
		this.plugin = plugin;
		commandList = new CommandMap();
		initializeAllCommands();
	}

	public CommandMap getCommandList() {
		return commandList;
	}

	public void setCommandList(CommandMap commandList) {
		this.commandList = commandList;
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
			// Execute command
			int cmdPage = 1;
			if (args.length > 1) {
				cmdPage = parsePageNumber(args[1]);
			}
			showAllCommands(sender, cmdPage);
			break;
		case "version":
			// Execute command
			String[] args_1 = { plugin.pluginVersion };
			sendLocalizedString(sender, "%pluginVersion%", args_1);
			break;
		case "makeplots":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				break;
			}
			Player player = (Player) sender;
			String uuid = player.getUniqueId().toString();
			if (plugin.flatMePlayers.getPlayer(uuid) == null) {
				plugin.flatMePlayers.add(uuid, new FlatMePlayer(plugin, player));
			}
			// Execute command
			String cmdWorld = args[1];
			World world = Bukkit.getWorld(cmdWorld);
			if (world == null) {
				String[] args_0 = { cmdWorld };
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%worldNotFound%", args_0);
				break;
			}
			int cmdRadius;
			try {
				cmdRadius = Integer.parseInt(args[2]);
			} catch (Exception e) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString(returnCorrectUsage(firstArg), null);
				break;
			}
			if (cmdRadius < 1) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%tooSmallRadius%", null);
				break;
			}
			// Region maker -> queue
			RegionMaker rm = new RegionMaker(plugin, plugin.flatMePlayers.getPlayer(uuid), cmdRadius, plugin.config.getInt("plotSize", 50), world);
			rm.run();
			// Kick off player queue
			PlayerQueue pq = plugin.flatMePlayers.getPlayer(uuid).getQueue();
			pq.run();
			break;
		default:
			// Execute command
			String[] args_0 = { firstArg };
			sendLocalizedString(sender, "%commandError%", args_0);
			sendLocalizedString(sender, returnCorrectUsage("help"), null);
		}
	}

	public String returnCorrectUsage(String command) {
		String[] args_0 = { commandList.getCommand(command).getUsage() };
		return plugin.configurator.resolveLocalizedString("%correctUsage%", args_0);
	}

	public void showAllCommands(CommandSender sender, int page) {
		List<String> temp = new ArrayList<String>();
		plugin.getLogger().info(Integer.toString(commandList.size()));
		for (int i = 0; i < commandList.size(); i++) {
			String[] args_0 = { commandList.getCommand(i).getUsage() };
			temp.add(plugin.configurator.resolveLocalizedString("%cmd_" + commandList.getCommandText(i) + "%", args_0));
		}
		String[] args_0 = { plugin.pluginTitle, plugin.pluginVersion };
		showTable(sender, plugin.configurator.resolveLocalizedString("%pluginTitle% &6- Plugin by Enatras", args_0), page, temp);
	}

	public void showTable(CommandSender sender, String title, int page, List<String> entrys) {
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
			entrys.add(plugin.configurator.resolveLocalizedString("%noResultsFound%", null));
		}
		if ((max + 1) > entrys.size()) {
			max = (entrys.size() - 1);
		}
		sendLocalizedString(sender, title, null);
		String args_0[] = { Integer.toString(page), Integer.toString(pages) };
		sendLocalizedString(sender, "%pageTitle%", args_0);
		for (int i = min; i <= max; i++) {
			sendLocalizedString(sender, entrys.get(i), null);
		}
	}

	private void initializeAllCommands() {
		commandList.add("help", new Command("flatme.player", "/flatme help [page]", 0));
		commandList.add("version", new Command("flatme.player", "/flatme version", 0));
		commandList.add("makeplots", new Command("flatme.admin", "/flatme makeplots <world> <radius>", 2));
	}

	private void sendLocalizedString(CommandSender sender, String input, String[] args) {
		sender.sendMessage(plugin.configurator.resolveLocalizedString(input, args));
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

	private void executeConsole(String cmd) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
	}

	private static int myRandom(int low, int high) {
		return (int) (Math.random() * (high - low) + low);
	}

}
