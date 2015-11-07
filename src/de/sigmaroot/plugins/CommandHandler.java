package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.User;
import com.earth2me.essentials.api.Economy;

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

	public boolean handleConsoleCommand(CommandSender console, String[] args) {
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].toLowerCase();
		}
		Command executedCommand;
		String firstArg = args[0];
		try {
			executedCommand = commandList.getCommand(firstArg);
		} catch (Exception e) {
			String[] args_0 = { firstArg };
			sendConsoleLocalizedString(console, "%commandError%", args_0);
			sendConsoleLocalizedString(console, returnCorrectUsage("help"), null);
			return false;
		}
		if (executedCommand == null) {
			String[] args_0 = { firstArg };
			sendConsoleLocalizedString(console, "%commandNotFound%", args_0);
			sendConsoleLocalizedString(console, returnCorrectUsage("help"), null);
			return false;
		}
		if (!executedCommand.enoughArguments(args)) {
			sendConsoleLocalizedString(console, returnCorrectUsage(firstArg), null);
			return false;
		}
		if (!console.hasPermission(executedCommand.getPermission())) {
			sendConsoleLocalizedString(console, "%noPermission%", null);
			return false;
		}
		boolean wasExecuted = false;
		switch (firstArg) {
		case "version":
			wasExecuted = true;
			// Execute command
			String[] args_0 = { plugin.PLUGIN_VERSION };
			sendConsoleLocalizedString(console, "%pluginVersion%", args_0);
			break;
		case "update":
			wasExecuted = true;
			// PLOT CHECK
			PlotCheck plotCheck_1 = new PlotCheck(plugin, null);
			if (!plotCheck_1.simpleCheckForCorrectWorld()) {
				String[] args_1 = { plugin.config_world };
				sendConsoleLocalizedString(console, "%worldNotFound%", args_1);
				break;
			}
			// Execute command
			sendConsoleLocalizedString(console, "%updatingPlots%", null);
			int removedRegions = plugin.worldGuardHandler.removeAllRegions(plotCheck_1.getWorld());
			String[] args_1 = { String.format("%d", removedRegions) };
			sendConsoleLocalizedString(console, "%removedRegions%", args_1);
			int createdRegions = plugin.worldGuardHandler.createAllRegions(plotCheck_1.getWorld());
			String[] args_1_1 = { String.format("%d", createdRegions) };
			sendConsoleLocalizedString(console, "%createdRegions%", args_1_1);
			sendConsoleLocalizedString(console, "%commandMayTakeAWhile%", null);
			BlockChanger blockChanger_1 = new BlockChanger(plugin, console, plotCheck_1.getWorld());
			for (int i = -plugin.config_radius; i < plugin.config_radius; i++) {
				for (int j = -plugin.config_radius; j < plugin.config_radius; j++) {
					PlotCheck plotCheck_1_1 = new PlotCheck(plugin, null);
					boolean isUsed = !plotCheck_1_1.checkForFreePlot(i, j);
					boolean isLocked = false;
					boolean isExpired = false;
					if (isUsed) {
						for (int k = 0; k < plugin.flatMePlayers.size(); k++) {
							for (int l = 0; l < plugin.flatMePlayers.getPlayer(k).getPlots().size(); l++) {
								if ((plugin.flatMePlayers.getPlayer(k).getPlots().get(l).getPlaceX() == i) && (plugin.flatMePlayers.getPlayer(k).getPlots().get(l).getPlaceY() == j)) {
									isLocked = plugin.flatMePlayers.getPlayer(k).getPlots().get(l).isLocked();
									isExpired = plugin.flatMePlayers.getPlayer(k).getPlots().get(l).isExpired();
								}
							}
						}
					}
					blockChanger_1.runPlot(i, j, isUsed, isExpired, isLocked);
				}
			}
			sendConsoleLocalizedString(console, "%plotsUpdated%", null);
			sendConsoleLocalizedString(console, "%commandHasBeenQueued%", null);
			blockChanger_1.getPlayerQueue().run();
			break;
		default:
			// Execute command
			wasExecuted = false;
			break;
		}
		return wasExecuted;
	}

	@SuppressWarnings("deprecation")
	public void handleCommand(CommandSender sender, String[] args) {
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].toLowerCase();
		}
		if (!(sender instanceof Player)) {
			if (handleConsoleCommand(sender, args)) {
				return;
			} else {
				sendConsoleLocalizedString(sender, "%commandOnlyPlayer%", null);
				return;
			}
		}
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		plugin.flatMePlayers.add(uuid);
		Command executedCommand;
		String firstArg = args[0];
		try {
			executedCommand = commandList.getCommand(firstArg);
		} catch (Exception e) {
			String[] args_0 = { firstArg };
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%commandError%", args_0);
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString(returnCorrectUsage("help"), null);
			return;
		}
		if (executedCommand == null) {
			String[] args_0 = { firstArg };
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%commandNotFound%", args_0);
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString(returnCorrectUsage("help"), null);
			return;
		}
		if (!executedCommand.enoughArguments(args)) {
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString(returnCorrectUsage(firstArg), null);
			return;
		}
		if (!sender.hasPermission(executedCommand.getPermission())) {
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%noPermission%", null);
			return;
		}
		switch (firstArg) {
		case "yes":
			// Run saved command
			if (plugin.flatMePlayers.getPlayer(uuid).getSecurityCommand() != null) {
				plugin.flatMePlayers.getPlayer(uuid).setAnsweredYes(true);
				handleCommand(sender, plugin.flatMePlayers.getPlayer(uuid).getSecurityCommand());
			}
			break;
		case "help":
			// TESTS
			int cmdPage_0 = 1;
			if (args.length > 1) {
				cmdPage_0 = parsePageNumber(args[1]);
			}
			// Execute command
			showAllCommands(sender, cmdPage_0);
			break;
		case "version":
			// Execute command
			String[] args_1 = { plugin.PLUGIN_VERSION };
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%pluginVersion%", args_1);
			break;
		case "create":
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid), args)) {
				break;
			}
			// PLOT-CHECK
			PlotCheck plotCheck_2 = new PlotCheck(plugin, null);
			if (!plotCheck_2.simpleCheckForCorrectWorld()) {
				String[] args_2 = { plugin.config_world };
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%worldNotFound%", args_2);
				break;
			}
			// TESTS
			if (plugin.config_radius < 1) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%tooSmallRadius%", null);
				break;
			}
			// Execute command
			BlockChanger blockChanger_2 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_2.getWorld());
			blockChanger_2.runCreate();
			blockChanger_2.getPlayerQueue().run();
			break;
		case "reload":
			// Execute command
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%reloadingConfiguration%", null);
			plugin.reloadConfiguration();
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%reloadedSuccessfully%", null);
			break;
		case "update":
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_4 = new PlotCheck(plugin, null);
			if (!plotCheck_4.simpleCheckForCorrectWorld()) {
				String[] args_4 = { plugin.config_world };
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%worldNotFound%", args_4);
				break;
			}
			// Execute command
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%updatingPlots%", null);
			int removedRegions = plugin.worldGuardHandler.removeAllRegions(plotCheck_4.getWorld());
			String[] args_4 = { String.format("%d", removedRegions) };
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%removedRegions%", args_4);
			int createdRegions = plugin.worldGuardHandler.createAllRegions(plotCheck_4.getWorld());
			String[] args_4_1 = { String.format("%d", createdRegions) };
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%createdRegions%", args_4_1);
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%commandMayTakeAWhile%", null);
			BlockChanger blockChanger_4 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_4.getWorld());
			for (int i = -plugin.config_radius; i < plugin.config_radius; i++) {
				for (int j = -plugin.config_radius; j < plugin.config_radius; j++) {
					PlotCheck plotCheck_4_1 = new PlotCheck(plugin, null);
					boolean isUsed = !plotCheck_4_1.checkForFreePlot(i, j);
					boolean isLocked = false;
					boolean isExpired = false;
					if (isUsed) {
						for (int k = 0; k < plugin.flatMePlayers.size(); k++) {
							for (int l = 0; l < plugin.flatMePlayers.getPlayer(k).getPlots().size(); l++) {
								if ((plugin.flatMePlayers.getPlayer(k).getPlots().get(l).getPlaceX() == i) && (plugin.flatMePlayers.getPlayer(k).getPlots().get(l).getPlaceY() == j)) {
									isLocked = plugin.flatMePlayers.getPlayer(k).getPlots().get(l).isLocked();
									isExpired = plugin.flatMePlayers.getPlayer(k).getPlots().get(l).isExpired();
								}
							}
						}
					}
					blockChanger_4.runPlot(i, j, isUsed, isExpired, isLocked);
				}
			}
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotsUpdated%", null);
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%commandHasBeenQueued%", null);
			blockChanger_4.getPlayerQueue().run();
			break;
		case "claim":
			// PLOT CHECK
			PlotCheck plotCheck_5 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid));
			if (!plotCheck_5.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_5.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_5.checkForFreePlot()) {
				break;
			}
			// TESTS
			if (plugin.flatMePlayers.getPlayer(uuid).getPlots().size() >= plugin.config_maxPlots) {
				if (!player.hasPermission("flatme.moreplots")) {
					plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%tooManyPlots%", null);
					break;
				}
			}
			// Execute command
			PlayerMap emptyMembers_5 = new PlayerMap(plugin);
			Long newExpire_5 = System.currentTimeMillis() + (plugin.config_daysPerPlot * 86400000L);
			Plot tempPlot_5 = new Plot(plugin, plotCheck_5.getPosX(), plotCheck_5.getPosY(), plugin.flatMePlayers.getPlayer(uuid), emptyMembers_5, newExpire_5, false);
			tempPlot_5.createWGRegion(plotCheck_5.getWorld());
			plugin.flatMePlayers.getPlayer(uuid).getPlots().add(tempPlot_5);
			plugin.configurator.saveAllPlots();
			BlockChanger blockChanger_5 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_5.getWorld());
			blockChanger_5.runPlot(plotCheck_5.getPosX(), plotCheck_5.getPosY(), true, false, false);
			plugin.flatMePlayers.getPlayer(uuid).setQueueSilence(true);
			blockChanger_5.getPlayerQueue().run();
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotClaimed%", null);
			break;
		case "autoclaim":
			// PLOT CHECK
			PlotCheck plotCheck_6 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid));
			if (!plotCheck_6.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_6.checkForNextPlot()) {
				break;
			}
			// TESTS
			if (plugin.flatMePlayers.getPlayer(uuid).getPlots().size() >= plugin.config_maxPlots) {
				if (!player.hasPermission("flatme.moreplots")) {
					plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%tooManyPlots%", null);
					break;
				}
			}
			// Execute command
			PlayerMap emptyMembers_6 = new PlayerMap(plugin);
			Long newExpire_6 = System.currentTimeMillis() + (plugin.config_daysPerPlot * 86400000L);
			Plot tempPlot_6 = new Plot(plugin, plotCheck_6.getPosX(), plotCheck_6.getPosY(), plugin.flatMePlayers.getPlayer(uuid), emptyMembers_6, newExpire_6, false);
			tempPlot_6.createWGRegion(plotCheck_6.getWorld());
			plugin.flatMePlayers.getPlayer(uuid).getPlots().add(tempPlot_6);
			plugin.configurator.saveAllPlots();
			BlockChanger blockChanger_6 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_6.getWorld());
			blockChanger_6.runPlot(plotCheck_6.getPosX(), plotCheck_6.getPosY(), true, false, false);
			plugin.flatMePlayers.getPlayer(uuid).setQueueSilence(true);
			blockChanger_6.getPlayerQueue().run();
			String[] args_6 = { String.format("%d", plotCheck_6.getPosX()), String.format("%d", plotCheck_6.getPosY()) };
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotAutoClaimed%", args_6);
			break;
		case "home":
			// PLOT CHECK
			PlotCheck plotCheck_7 = new PlotCheck(plugin, null);
			if (!plotCheck_7.simpleCheckForCorrectWorld()) {
				String[] args_7 = { plugin.config_world };
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%worldNotFound%", args_7);
				break;
			}
			// TESTS
			if (plugin.flatMePlayers.getPlayer(uuid).getPlots().size() == 0) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%noPlotClaimed%", null);
				break;
			}
			// Execute command
			int portX_7 = plugin.flatMePlayers.getPlayer(uuid).getPlots().get(0).getPlaceX() * plugin.config_jumpInterval;
			int portY_7 = plugin.flatMePlayers.getPlayer(uuid).getPlots().get(0).getPlaceY() * plugin.config_jumpInterval;
			Location portLocation_7 = new Location(plotCheck_7.getWorld(), portX_7, (plugin.config_lvlHeight + 1), portY_7);
			portLocation_7.setYaw(-45F);
			player.teleport(portLocation_7);
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%teleportToFirstPlot%", null);
			break;
		case "delete":
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_8 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid));
			if (!plotCheck_8.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_8.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_8.checkForNotFreePlot()) {
				break;
			}
			// Execute command
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_8.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_8.getPosY())) {
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_8.getWorld());
						plugin.flatMePlayers.getPlayer(i).getPlots().remove(j);
						plugin.configurator.saveAllPlots();
						BlockChanger blockChanger_8 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_8.getWorld());
						blockChanger_8.runPlot(plotCheck_8.getPosX(), plotCheck_8.getPosY(), false, false, false);
						plugin.flatMePlayers.getPlayer(uuid).setQueueSilence(true);
						blockChanger_8.getPlayerQueue().run();
					}
				}
			}
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotDeleted%", null);
			break;
		case "add":
			// PLOT CHECK
			PlotCheck plotCheck_9 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid));
			if (!plotCheck_9.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_9.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_9.checkForRightOwner()) {
				break;
			}
			// TESTS
			Player addPlayer = plugin.getServer().getPlayer(args[1]);
			UUID uuid_9_1 = null;
			if (addPlayer == null) {
				User essentialsUser = plugin.essAPI.getOfflineUser(args[1]);
				if (essentialsUser == null) {
					String[] args_9 = { args[1] };
					plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%playerNotFound%", args_9);
					break;
				} else {
					uuid_9_1 = essentialsUser.getConfigUUID();
				}
			} else {
				uuid_9_1 = addPlayer.getUniqueId();
			}
			// Execute command
			plugin.flatMePlayers.add(uuid_9_1);
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_9.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_9.getPosY())) {
						if (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getMembers().getPlayer(uuid_9_1) != null) {
							String[] args_9 = { args[1] };
							plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%playerAlreadyMember%", args_9);
						} else {
							plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getMembers().add(uuid_9_1);
							plugin.configurator.saveAllPlots();
							plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_9.getWorld());
							plugin.flatMePlayers.getPlayer(i).getPlots().get(j).createWGRegion(plotCheck_9.getWorld());
							String[] args_9 = { args[1] };
							plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%playerAdded%", args_9);
						}
					}
				}
			}
			break;
		case "remove":
			// PLOT CHECK
			PlotCheck plotCheck_10 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid));
			if (!plotCheck_10.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_10.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_10.checkForRightOwner()) {
				break;
			}
			// TESTS
			Player removePlayer = plugin.getServer().getPlayer(args[1]);
			UUID uuid_10_1 = null;
			if (removePlayer == null) {
				User essentialsUser = plugin.essAPI.getOfflineUser(args[1]);
				if (essentialsUser == null) {
					String[] args_9 = { args[1] };
					plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%playerNotFound%", args_9);
					break;
				} else {
					uuid_10_1 = essentialsUser.getConfigUUID();
				}
			} else {
				uuid_10_1 = removePlayer.getUniqueId();
			}
			// Execute command
			plugin.flatMePlayers.add(uuid_10_1);
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_10.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_10.getPosY())) {
						if (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getMembers().getPlayer(uuid_10_1) == null) {
							String[] args_10 = { args[1] };
							plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%playerNotAMember%", args_10);
						} else {
							plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getMembers().remove(uuid_10_1);
							plugin.configurator.saveAllPlots();
							plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_10.getWorld());
							plugin.flatMePlayers.getPlayer(i).getPlots().get(j).createWGRegion(plotCheck_10.getWorld());
							String[] args_10 = { args[1] };
							plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%playerRemoved%", args_10);
						}
					}
				}
			}
			break;
		case "extend":
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_11 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid));
			if (!plotCheck_11.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_11.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_11.checkForRightOwner()) {
				break;
			}
			// TESTS
			Double costs = Double.parseDouble(Integer.toString(plugin.config_extendCost));
			Double balance = 0D;
			try {
				balance = Economy.getMoney(plugin.flatMePlayers.getPlayer(uuid).getDisplayName());
			} catch (Exception e0) {
				e0.printStackTrace();
			}
			if (balance < costs) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%notEnoughMoney%", null);
				break;
			}
			// Execute command
			try {
				Economy.subtract(plugin.flatMePlayers.getPlayer(uuid).getDisplayName(), costs);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Long newExpire_11 = System.currentTimeMillis() + (plugin.config_daysPerPlot * 86400000L);
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_11.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_11.getPosY())) {
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).setExpireDate(newExpire_11);
						plugin.configurator.saveAllPlots();
						BlockChanger blockChanger_11 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_11.getWorld());
						blockChanger_11.runPlot(plotCheck_11.getPosX(), plotCheck_11.getPosY(), true, plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired(), plugin.flatMePlayers
								.getPlayer(i).getPlots().get(j).isLocked());
						plugin.flatMePlayers.getPlayer(uuid).setQueueSilence(true);
						blockChanger_11.getPlayerQueue().run();
						String[] args_11 = { plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableExpireDate() };
						plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotExtended%", args_11);
					}
				}
			}
			break;
		case "lock":
			// PLOT CHECK
			PlotCheck plotCheck_12 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid));
			if (!plotCheck_12.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_12.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_12.checkForNotFreePlot()) {
				break;
			}
			// Execute command
			boolean nowLocked = false;
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_12.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_12.getPosY())) {
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).toggleLocked();
						plugin.configurator.saveAllPlots();
						nowLocked = plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isLocked();
						BlockChanger blockChanger_12 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_12.getWorld());
						blockChanger_12.runPlot(plotCheck_12.getPosX(), plotCheck_12.getPosY(), true, plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired(), plugin.flatMePlayers
								.getPlayer(i).getPlots().get(j).isLocked());
						plugin.flatMePlayers.getPlayer(uuid).setQueueSilence(true);
						blockChanger_12.getPlayerQueue().run();
					}
				}
			}
			if (nowLocked) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotLockedTrue%", null);
			} else {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotLockedFalse%", null);
			}
			break;
		case "info":
			// PLOT CHECK
			PlotCheck plotCheck_13 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid));
			if (!plotCheck_13.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_13.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_13.checkForNotFreePlot()) {
				break;
			}
			// Execute command
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_13.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_13.getPosY())) {
						plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%infoHeader%", null);
						String[] args_13 = { String.format("%d", plotCheck_13.getPosX()), String.format("%d", plotCheck_13.getPosY()) };
						plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%infoID%", args_13);
						String[] args_13_1 = { plugin.flatMePlayers.getPlayer(i).getDisplayName() };
						plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%infoOwner%", args_13_1);
						String[] args_13_2 = { plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableMemberList() };
						plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%infoMembers%", args_13_2);
						String arg_13_3 = "";
						if (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired()) {
							arg_13_3 = ChatColor.DARK_RED + plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableExpireDate();
						} else {
							arg_13_3 = ChatColor.DARK_GREEN + plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableExpireDate();
						}
						String[] args_13_3 = { arg_13_3 };
						plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%infoExpire%", args_13_3);
						String arg_13_4 = "";
						if (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isLocked()) {
							arg_13_4 = ChatColor.DARK_GREEN + plugin.configurator.resolveLocalizedString("%answerYes%", null);
						} else {
							arg_13_4 = ChatColor.DARK_RED + plugin.configurator.resolveLocalizedString("%answerNo%", null);
						}
						String[] args_13_4 = { arg_13_4 };
						plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%infoLocked%", args_13_4);
					}
				}
			}
			break;
		case "check":
			// Execute command
			int cmdPage_14 = 1;
			if (args.length > 1) {
				cmdPage_14 = parsePageNumber(args[1]);
			}
			List<String> expiredPlots = new ArrayList<String>();
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired()) && (!plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isLocked())) {
						expiredPlots.add("Plot " + String.format("%d", plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX()) + ","
								+ String.format("%d", plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY()) + " (" + plugin.flatMePlayers.getPlayer(i).getDisplayName() + ") - "
								+ plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableExpireDate());
					}
				}
			}
			showTable(sender, plugin.configurator.resolveLocalizedString("%expiredPlots%", null), cmdPage_14, expiredPlots);
			break;
		case "clean":
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_15 = new PlotCheck(plugin, null);
			if (!plotCheck_15.simpleCheckForCorrectWorld()) {
				String[] args_15 = { plugin.config_world };
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%worldNotFound%", args_15);
				break;
			}
			// TESTS
			int cmdCount_15 = 1;
			if (args.length > 1) {
				cmdCount_15 = parseCount(args[1]);
			}
			if (cmdCount_15 < 1) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString(returnCorrectUsage("clean"), null);
				break;
			}
			// Execute command
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%commandMayTakeAWhile%", null);
			int toDo = 0;
			BlockChanger blockChanger_15 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_15.getWorld());
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				if (toDo >= cmdCount_15) {
					break;
				}
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if (toDo >= cmdCount_15) {
						break;
					}
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired()) && (!plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isLocked())) {
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_15.getWorld());
						blockChanger_15.runRegen(plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX(), plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY());
						int oldX = plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX();
						int oldY = plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY();
						plugin.flatMePlayers.getPlayer(i).getPlots().remove(j);
						blockChanger_15.runPlot(oldX, oldY, false, false, false);
						toDo++;
					}
				}
			}
			plugin.configurator.saveAllPlots();
			String[] args_15 = { String.format("%d", toDo) };
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotsCleaned%", args_15);
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%commandHasBeenQueued%", null);
			blockChanger_15.getPlayerQueue().run();
			break;
		case "regen":
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_16 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid));
			if (!plotCheck_16.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_16.checkForPlotInArea()) {
				break;
			}
			// Execute command
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%commandMayTakeAWhile%", null);
			BlockChanger blockChanger_16 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_16.getWorld());
			blockChanger_16.runRegen(plotCheck_16.getPosX(), plotCheck_16.getPosY());
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotRegenerated%", null);
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%commandHasBeenQueued%", null);
			blockChanger_16.getPlayerQueue().run();
			break;
		case "weclean":
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_18 = new PlotCheck(plugin, null);
			if (!plotCheck_18.simpleCheckForCorrectWorld()) {
				String[] args_18 = { plugin.config_world };
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%worldNotFound%", args_18);
				break;
			}
			// TESTS
			int cmdCount_18 = 1;
			if (args.length > 1) {
				cmdCount_18 = parseCount(args[1]);
			}
			if (cmdCount_18 < 1) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString(returnCorrectUsage("weclean"), null);
				break;
			}
			// Execute command
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%commandMayTakeAWhile%", null);
			int toDo_18 = 0;
			BlockChanger blockChanger_18 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_18.getWorld());
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				if (toDo_18 >= cmdCount_18) {
					break;
				}
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if (toDo_18 >= cmdCount_18) {
						break;
					}
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired()) && (!plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isLocked())) {
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_18.getWorld());
						blockChanger_18.runWEregen(plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX(), plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY());
						int oldX = plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX();
						int oldY = plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY();
						plugin.flatMePlayers.getPlayer(i).getPlots().remove(j);
						blockChanger_18.runPlot(oldX, oldY, false, false, false);
						toDo_18++;
					}
				}
			}
			plugin.configurator.saveAllPlots();
			String[] args_18 = { String.format("%d", toDo_18) };
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotsCleaned%", args_18);
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%commandHasBeenQueued%", null);
			blockChanger_18.getPlayerQueue().run();
			break;
		case "weregen":
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_17 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid));
			if (!plotCheck_17.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_17.checkForPlotInArea()) {
				break;
			}
			// Execute command
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%commandMayTakeAWhile%", null);
			BlockChanger blockChanger_17 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_17.getWorld());
			blockChanger_17.runWEregen(plotCheck_17.getPosX(), plotCheck_17.getPosY());
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotRegenerated%", null);
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%commandHasBeenQueued%", null);
			break;
		case "teleport":
			// TESTS
			int cmdPosX_19 = 0;
			cmdPosX_19 = parsePosition(args[1]);
			int cmdPosY_19 = 0;
			cmdPosY_19 = parsePosition(args[2]);
			// PLOT CHECK
			PlotCheck plotCheck_19 = new PlotCheck(plugin, null);
			if (!plotCheck_19.simpleCheckForCorrectWorld()) {
				String[] args_19 = { plugin.config_world };
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%worldNotFound%", args_19);
				break;
			}
			if (!plotCheck_19.checkForPlotInArea(cmdPosX_19, cmdPosY_19)) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%wouldBeOutOfArea%", null);
				break;
			}
			// Execute command
			int portX_19 = cmdPosX_19 * plugin.config_jumpInterval;
			int portY_19 = cmdPosY_19 * plugin.config_jumpInterval;
			Location portLocation_19 = new Location(plotCheck_19.getWorld(), portX_19, (plugin.config_lvlHeight + 1), portY_19);
			portLocation_19.setYaw(-45F);
			player.teleport(portLocation_19);
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%teleportToPlot%", null);
			break;
		case "show":
			// Execute command
			plugin.flatMePlayers.getPlayer(uuid).setQueueSilence(false);
			break;
		case "repair":
			// TESTS
			int cmdPosX_21 = 0;
			cmdPosX_21 = parsePosition(args[1]);
			int cmdPosY_21 = 0;
			cmdPosY_21 = parsePosition(args[2]);
			// PLOT CHECK
			PlotCheck plotCheck_21 = new PlotCheck(plugin, null);
			if (!plotCheck_21.simpleCheckForCorrectWorld()) {
				String[] args_21 = { plugin.config_world };
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%worldNotFound%", args_21);
				break;
			}
			if (!plotCheck_21.checkForPlotInArea(cmdPosX_21, cmdPosY_21)) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%wouldBeOutOfArea%", null);
				break;
			}
			// Execute command
			BlockChanger blockChanger_21 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_21.getWorld());
			blockChanger_21.runRepair(cmdPosX_21, cmdPosY_21);
			blockChanger_21.getPlayerQueue().run();
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%pathRepaired%", null);
			break;
		case "move":
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid), args)) {
				break;
			}
			// TESTS
			int cmdPosX_22 = 0;
			cmdPosX_22 = parsePosition(args[1]);
			int cmdPosY_22 = 0;
			cmdPosY_22 = parsePosition(args[2]);
			// PLOT CHECK
			PlotCheck plotCheck_22 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid));
			if (!plotCheck_22.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_22.checkForPlotInArea(cmdPosX_22, cmdPosY_22)) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%wouldBeOutOfArea%", null);
				break;
			}
			if (!plotCheck_22.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_22.checkForNotFreePlot()) {
				break;
			}
			if (!plotCheck_22.checkForFreePlot(cmdPosX_22, cmdPosY_22)) {
				plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotAlreadyOwned%", null);
				break;
			}
			plugin.getLogger().info(plotCheck_22.getPosX() + "," + plotCheck_22.getPosY());
			// Execute command
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_22.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_22.getPosY())) {
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_22.getWorld());
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).setPlaceX(cmdPosX_22);
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).setPlaceY(cmdPosY_22);
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).createWGRegion(plotCheck_22.getWorld());
						plugin.configurator.saveAllPlots();
						RunnableMove plotMove = new RunnableMove(plugin, player, plotCheck_22.getPosX(), plotCheck_22.getPosY(), cmdPosX_22, cmdPosY_22, plotCheck_22.getWorld());
						plotMove.run();
						BlockChanger blockChanger_22 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_22.getWorld());
						blockChanger_22.runPlot(plotCheck_22.getPosX(), plotCheck_22.getPosY(), false, false, false);
						blockChanger_22.runPlot(cmdPosX_22, cmdPosY_22, true, plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired(), plugin.flatMePlayers.getPlayer(i).getPlots().get(j)
								.isLocked());
						plugin.flatMePlayers.getPlayer(uuid).setQueueSilence(true);
						blockChanger_22.getPlayerQueue().run();
					}
				}
			}
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotMoved%", null);
			break;
		case "updateplot":
			// PLOT CHECK
			PlotCheck plotCheck_23 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid));
			if (!plotCheck_23.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_23.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_23.checkForRightOwner()) {
				break;
			}
			// Execute command
			BlockChanger blockChanger_23 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid), plotCheck_23.getWorld());
			boolean foundAOwnedPlot = false;
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_23.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_23.getPosY())) {
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_23.getWorld());
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).createWGRegion(plotCheck_23.getWorld());
						foundAOwnedPlot = true;
						blockChanger_23.runPlot(plotCheck_23.getPosX(), plotCheck_23.getPosY(), true, plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired(), plugin.flatMePlayers
								.getPlayer(i).getPlots().get(j).isLocked());

					}
				}
			}
			if (!foundAOwnedPlot) {
				blockChanger_23.runPlot(plotCheck_23.getPosX(), plotCheck_23.getPosY(), false, false, false);
			}
			plugin.flatMePlayers.getPlayer(uuid).setQueueSilence(true);
			blockChanger_23.getPlayerQueue().run();
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%plotUpdated%", null);
			break;
		default:
			// Execute command
			String[] args_x = { firstArg };
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString("%commandError%", args_x);
			plugin.flatMePlayers.getPlayer(uuid).sendLocalizedString(returnCorrectUsage("help"), null);
			break;
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
			if (!sender.hasPermission(commandList.getCommand(i).getPermission())) {
				continue;
			}
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
		commandList.add("add", new Command("flatme.player", "/flatme add <playername>", 1));
		commandList.add("autoclaim", new Command("flatme.player", "/flatme autoclaim", 0));
		commandList.add("check", new Command("flatme.admin", "/flatme check [page]", 0));
		commandList.add("claim", new Command("flatme.player", "/flatme claim", 0));
		commandList.add("clean", new Command("flatme.admin", "/flatme clean <count>", 1));
		commandList.add("create", new Command("flatme.admin", "/flatme create", 0));
		commandList.add("delete", new Command("flatme.admin", "/flatme delete", 0));
		commandList.add("extend", new Command("flatme.player", "/flatme extend", 0));
		commandList.add("help", new Command("flatme.player", "/flatme help [page]", 0));
		commandList.add("home", new Command("flatme.player", "/flatme home", 0));
		commandList.add("info", new Command("flatme.player", "/flatme info", 0));
		commandList.add("lock", new Command("flatme.admin", "/flatme lock", 0));
		commandList.add("move", new Command("flatme.admin", "/flatme move <x> <y>", 2));
		commandList.add("regen", new Command("flatme.admin", "/flatme regen", 0));
		commandList.add("reload", new Command("flatme.admin", "/flatme reload", 0));
		commandList.add("remove", new Command("flatme.player", "/flatme remove <playername>", 1));
		commandList.add("repair", new Command("flatme.admin", "/flatme repair <x> <y>", 2));
		commandList.add("show", new Command("flatme.admin", "/flatme show", 0));
		commandList.add("teleport", new Command("flatme.admin", "/flatme teleport <x> <y>", 2));
		commandList.add("updateplot", new Command("flatme.player", "/flatme updateplot", 0));
		commandList.add("update", new Command("flatme.admin", "/flatme update", 0));
		commandList.add("version", new Command("flatme.player", "/flatme version", 0));
		commandList.add("weclean", new Command("flatme.admin", "/flatme weclean <count>", 1));
		commandList.add("weregen", new Command("flatme.admin", "/flatme weregen", 0));
		commandList.add("yes", new Command("flatme.player", "/flatme yes", 0));
	}

	private void sendLocalizedString(CommandSender sender, String input, String[] args) {
		sender.sendMessage(plugin.configurator.resolveLocalizedString(input, args));
	}

	private void sendConsoleLocalizedString(CommandSender sender, String input, String[] args) {
		sender.sendMessage("[" + plugin.PLUGIN_TITLE + "] " + plugin.configurator.resolveLocalizedString(input, args));
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

	private int parseCount(String test) {
		int value;
		try {
			value = Integer.parseInt(test);
		} catch (Exception e) {
			return -1;
		}
		return value;
	}

	private int parsePosition(String test) {
		int value;
		try {
			value = Integer.parseInt(test);
		} catch (Exception e) {
			return 0;
		}
		return value;
	}

}
