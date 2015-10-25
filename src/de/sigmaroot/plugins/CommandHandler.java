package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
			String[] args_1 = { plugin.PLUGIN_VERSION };
			sendLocalizedString(sender, "%pluginVersion%", args_1);
			break;
		case "create":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				break;
			}
			Player player = (Player) sender;
			UUID uuid = player.getUniqueId();
			if (plugin.flatMePlayers.getPlayer(uuid) == null) {
				plugin.flatMePlayers.add(uuid, new FlatMePlayer(plugin, player));
			}
			// Execute command
			int cmdRadius;
			try {
				cmdRadius = Integer.parseInt(args[1]);
			} catch (Exception e) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString(returnCorrectUsage(firstArg), null);
				break;
			}
			if (cmdRadius < 1) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%tooSmallRadius%", null);
				break;
			}
			// Needed config settings
			String configWorld = plugin.config.getString("world", "world");
			World world = Bukkit.getWorld(configWorld);
			if (world == null) {
				String[] args_0 = { configWorld };
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%worldNotFound%", args_0);
				break;
			}
			// Region maker -> queue
			FieldCreator fieldCreator = new FieldCreator(plugin, plugin.flatMePlayers.getPlayer(uuid), cmdRadius, world);
			fieldCreator.runCreate();
			// Kick off player queue
			PlayerQueue playerQueue = plugin.flatMePlayers.getPlayer(uuid).getQueue();
			playerQueue.run();
			break;
		default:
			// Execute command
			String[] args_0 = { firstArg };
			sendLocalizedString(sender, "%commandError%", args_0);
			sendLocalizedString(sender, returnCorrectUsage("help"), null);
		}
	}

	public void executeConsole(String cmd) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
	}

	public String returnCorrectUsage(String command) {
		String[] args_0 = { commandList.getCommand(command).getUsage() };
		return plugin.configurator.resolveLocalizedString("%correctUsage%", args_0);
	}

	public void showAllCommands(CommandSender sender, int page) {
		List<String> temp = new ArrayList<String>();
		for (int i = 0; i < commandList.size(); i++) {
			String[] args_0 = { commandList.getCommand(i).getUsage() };
			temp.add(plugin.configurator.resolveLocalizedString("%cmd_" + commandList.getCommandText(i) + "%", args_0));
		}
		String[] args_0 = { plugin.PLUGIN_TITLE, plugin.PLUGIN_VERSION };
		showTable(sender, plugin.configurator.resolveLocalizedString("%pluginTitle% &6- Plugin by Enatras", args_0), page, temp);
	}

	public void showTable(CommandSender sender, String title, int page, List<String> entrys) {
		int show_entrys_per_page = plugin.config.getInt("maxResultsPerPage", 5);
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
		String args_0[] = { String.format("%02d", page), String.format("%02d", pages) };
		sendLocalizedString(sender, "%pageTitle%", args_0);
		for (int i = min; i <= max; i++) {
			sendLocalizedString(sender, entrys.get(i), null);
		}
	}

	private void initializeAllCommands() {
		commandList.add("help", new Command("flatme.player", "/flatme help [page]", 0));
		commandList.add("version", new Command("flatme.player", "/flatme version", 0));
		commandList.add("create", new Command("flatme.admin", "/flatme create <radius>", 1));
		commandList.add("generate", new Command("flatme.admin", "/flatme create <world> <radius>", 2));
		commandList.add("fillup", new Command("flatme.admin", "/flatme create <world> <radius>", 2));
		commandList.add("reload", new Command("flatme.admin", "/flatme create <world> <radius>", 2));
		commandList.add("check", new Command("flatme.admin", "/flatme create <world> <radius>", 2));
		commandList.add("clean", new Command("flatme.admin", "/flatme create <world> <radius>", 2));
		commandList.add("claim", new Command("flatme.admin", "/flatme create <world> <radius>", 2));
		commandList.add("home", new Command("flatme.admin", "/flatme create <world> <radius>", 2));
		commandList.add("add", new Command("flatme.admin", "/flatme create <world> <radius>", 2));
		commandList.add("remove", new Command("flatme.admin", "/flatme create <world> <radius>", 2));
		commandList.add("extend", new Command("flatme.admin", "/flatme create <world> <radius>", 2));
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

}
