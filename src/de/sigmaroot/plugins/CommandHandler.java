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
	private PlayerQueue consoleQueue;

	public CommandHandler(FlatMe plugin) {
		super();
		this.plugin = plugin;
		commandList = new CommandMap();
		initializeAllCommands();
		consoleQueue = new PlayerQueue(plugin);
	}

	public CommandMap getCommandList() {
		return commandList;
	}

	public void setCommandList(CommandMap commandList) {
		this.commandList = commandList;
	}

	public PlayerQueue getConsoleQueue() {
		return consoleQueue;
	}

	public void setConsoleQueue(PlayerQueue consoleQueue) {
		this.consoleQueue = consoleQueue;
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
		if (!((console.hasPermission(executedCommand.getPermission())) || (console.hasPermission("flatme.command." + firstArg)))) {
			sendConsoleLocalizedString(console, "%noPermission%", null);
			return false;
		}
		boolean wasExecuted = false;
		switch (firstArg) {
		case "version":
			// SIZE 0
			wasExecuted = true;
			// EXECUTE COMMAND
			// #1: Display version info
			String[] args_0 = { plugin.PLUGIN_VERSION };
			sendConsoleLocalizedString(console, "%pluginVersion%", args_0);
			break;
		case "updateall":
			// SIZE 2
			wasExecuted = true;
			// PLOT CHECK
			PlotCheck plotCheck_1 = new PlotCheck(plugin, null);
			if (!plotCheck_1.simpleCheckForCorrectWorld()) {
				String[] args_1 = { plugin.config_world };
				sendConsoleLocalizedString(console, "%worldNotFound%", args_1);
				break;
			}
			// EXECUTE COMMAND
			sendConsoleLocalizedString(console, "%updatingPlots%", null);
			sendConsoleLocalizedString(console, "%commandMayTakeAWhile%", null);
			// #1: Remove WG regions
			int removedRegions = plugin.worldGuardHandler.removeAllRegions(plotCheck_1.getWorld());
			String[] args_1 = { String.format("%d", removedRegions) };
			sendConsoleLocalizedString(console, "%removedRegions%", args_1);
			// #2: Create WG regions
			int createdRegions = plugin.worldGuardHandler.createAllRegions(plotCheck_1.getWorld());
			String[] args_1_1 = { String.format("%d", createdRegions) };
			sendConsoleLocalizedString(console, "%createdRegions%", args_1_1);
			// #3: Add tasks
			for (int i = -plugin.config_radius; i < plugin.config_radius; i++) {
				for (int j = -plugin.config_radius; j < plugin.config_radius; j++) {
					consoleQueue.addTask(i, j, plotCheck_1.getWorld(), QueueTaskType.CREATE_PLOT_BORDER);
				}
			}
			consoleQueue.addTask(0, 0, null, QueueTaskType.CONSOLE_MESSAGE, plugin.configurator.resolveLocalizedString("%allPlotsUpdated%", null));
			sendConsoleLocalizedString(console, "%commandHasBeenQueued%", null);
			sendConsoleLocalizedString(console, "%plotsUpdated%", null);
			// #4: Kick off
			consoleQueue.runTaskQueue();
			break;
		default:
			// SIZE 0
			// EXECUTE COMMAND
			// #1: Return false
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
		FlatMePlayer executingPlayer = plugin.flatMePlayers.getPlayer(uuid);
		Command executedCommand;
		String firstArg = args[0];
		try {
			executedCommand = commandList.getCommand(firstArg);
		} catch (Exception e) {
			String[] args_0 = { firstArg };
			executingPlayer.sendLocalizedString("%commandError%", args_0);
			executingPlayer.sendLocalizedString(returnCorrectUsage("help"), null);
			return;
		}
		if (executedCommand == null) {
			String[] args_0 = { firstArg };
			executingPlayer.sendLocalizedString("%commandNotFound%", args_0);
			executingPlayer.sendLocalizedString(returnCorrectUsage("help"), null);
			return;
		}
		if (!executedCommand.enoughArguments(args)) {
			executingPlayer.sendLocalizedString(returnCorrectUsage(firstArg), null);
			return;
		}
		if (!((sender.hasPermission(executedCommand.getPermission())) || (sender.hasPermission("flatme.command." + firstArg)))) {
			executingPlayer.sendLocalizedString("%noPermission%", null);
			return;
		}
		switch (firstArg) {
		case "yes":
			// SIZE 0
			// RUN SAVED COMMAND
			if (executingPlayer.getSecurityCommand() != null) {
				executingPlayer.setAnsweredYes(true);
				handleCommand(sender, executingPlayer.getSecurityCommand());
			}
			break;
		case "help":
			// SIZE 0
			// TESTS
			int cmdPage_0 = 1;
			if (args.length > 1) {
				cmdPage_0 = parsePageNumber(args[1]);
			}
			// EXECUTE COMMAND
			// #1: Show all commands
			showAllCommands(sender, cmdPage_0);
			break;
		case "version":
			// SIZE 0
			// EXECUTE COMMAND
			// #1: Display version info
			String[] args_1 = { plugin.PLUGIN_VERSION };
			executingPlayer.sendLocalizedString("%pluginVersion%", args_1);
			break;
		case "create":
			// SIZE 2
			// Security Check
			if (!securityCheck(executingPlayer, args)) {
				break;
			}
			// PLOT-CHECK
			PlotCheck plotCheck_2 = new PlotCheck(plugin, null);
			if (!plotCheck_2.simpleCheckForCorrectWorld()) {
				String[] args_2 = { plugin.config_world };
				executingPlayer.sendLocalizedString("%worldNotFound%", args_2);
				break;
			}
			// TESTS
			if (plugin.config_radius < 1) {
				executingPlayer.sendLocalizedString("%tooSmallRadius%", null);
				break;
			}
			// EXECUTE COMMAND
			// #1: Large?
			if (plugin.config_radius > 20) {
				executingPlayer.sendLocalizedString("%commandCreateLarge%", null);
			}
			// #2: Add tasks
			executingPlayer.sendLocalizedString("%commandMayTakeAWhile%", null);
			for (int i = -plugin.config_radius; i <= plugin.config_radius; i++) {
				for (int j = -plugin.config_radius; j <= plugin.config_radius; j++) {
					executingPlayer.getQueue().addTask(i, j, plotCheck_2.getWorld(), QueueTaskType.CREATE_RUNWAY);
				}
			}
			executingPlayer.getQueue().addTask(0, 0, plotCheck_2.getWorld(), QueueTaskType.CREATE_AREA_BORDER);
			executingPlayer.sendLocalizedString("%commandHasBeenQueued%", null);
			executingPlayer.sendLocalizedString("%plotAreaCreated%", null);
			// #3: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "reload":
			// SIZE 0
			// EXECUTE COMMAND
			// #1: Kick off reloading
			executingPlayer.sendLocalizedString("%reloadingConfiguration%", null);
			plugin.reloadConfiguration();
			executingPlayer.sendLocalizedString("%reloadedSuccessfully%", null);
			break;
		case "updateall":
			// SIZE 2
			// Security Check
			if (!securityCheck(executingPlayer, args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_4 = new PlotCheck(plugin, null);
			if (!plotCheck_4.simpleCheckForCorrectWorld()) {
				String[] args_4 = { plugin.config_world };
				executingPlayer.sendLocalizedString("%worldNotFound%", args_4);
				break;
			}
			// EXECUTE COMMAND
			executingPlayer.sendLocalizedString("%updatingPlots%", null);
			executingPlayer.sendLocalizedString("%commandMayTakeAWhile%", null);
			// #1: Remove WG regions
			int removedRegions = plugin.worldGuardHandler.removeAllRegions(plotCheck_4.getWorld());
			String[] args_4 = { String.format("%d", removedRegions) };
			executingPlayer.sendLocalizedString("%removedRegions%", args_4);
			// #2: Create WG regions
			int createdRegions = plugin.worldGuardHandler.createAllRegions(plotCheck_4.getWorld());
			String[] args_4_1 = { String.format("%d", createdRegions) };
			executingPlayer.sendLocalizedString("%createdRegions%", args_4_1);
			// #3: Find plots
			for (int i = -plugin.config_radius; i < plugin.config_radius; i++) {
				for (int j = -plugin.config_radius; j < plugin.config_radius; j++) {
					// #4: Add tasks
					executingPlayer.getQueue().addTask(i, j, plotCheck_4.getWorld(), QueueTaskType.CREATE_PLOT_BORDER);
				}
			}
			executingPlayer.sendLocalizedString("%commandHasBeenQueued%", null);
			executingPlayer.sendLocalizedString("%plotsUpdated%", null);
			// #5: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "claim":
			// SIZE 1
			// PLOT CHECK
			PlotCheck plotCheck_5 = new PlotCheck(plugin, executingPlayer);
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
			if (executingPlayer.getPlots().size() >= plugin.config_plotsPerUser) {
				if (!player.hasPermission("flatme.moreplots")) {
					executingPlayer.sendLocalizedString("%tooManyPlots%", null);
					break;
				}
			}
			// EXECUTE COMMAND
			// #1: Create plot
			PlayerMap emptyMembers_5 = new PlayerMap(plugin);
			Long newExpire_5 = System.currentTimeMillis() + (plugin.config_daysPerPlot * 86400000L);
			Plot tempPlot_5 = new Plot(plugin, plotCheck_5.getPosX(), plotCheck_5.getPosY(), executingPlayer, emptyMembers_5, newExpire_5, false);
			// #2: Create WG region
			tempPlot_5.createWGRegion(plotCheck_5.getWorld());
			// #3: Add plot and save all plots
			executingPlayer.getPlots().add(tempPlot_5);
			plugin.configurator.saveAllPlots();
			// #4: Add tasks
			executingPlayer.getQueue().addTask(plotCheck_5.getPosX(), plotCheck_5.getPosY(), plotCheck_5.getWorld(), QueueTaskType.CREATE_PLOT_BORDER);
			executingPlayer.sendLocalizedString("%plotClaimed%", null);
			// #5: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "autoclaim":
			// SIZE 1
			// PLOT CHECK
			PlotCheck plotCheck_6 = new PlotCheck(plugin, executingPlayer);
			if (!plotCheck_6.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_6.checkForNextPlot()) {
				break;
			}
			// TESTS
			if (executingPlayer.getPlots().size() >= plugin.config_plotsPerUser) {
				if (!player.hasPermission("flatme.moreplots")) {
					executingPlayer.sendLocalizedString("%tooManyPlots%", null);
					break;
				}
			}
			// EXECUTE COMMAND
			// #1: Create plot
			PlayerMap emptyMembers_6 = new PlayerMap(plugin);
			Long newExpire_6 = System.currentTimeMillis() + (plugin.config_daysPerPlot * 86400000L);
			Plot tempPlot_6 = new Plot(plugin, plotCheck_6.getPosX(), plotCheck_6.getPosY(), executingPlayer, emptyMembers_6, newExpire_6, false);
			// #2: Create WG region
			tempPlot_6.createWGRegion(plotCheck_6.getWorld());
			// #3: Add plot and save all plots
			executingPlayer.getPlots().add(tempPlot_6);
			plugin.configurator.saveAllPlots();
			// #4: Add tasks
			executingPlayer.getQueue().addTask(plotCheck_6.getPosX(), plotCheck_6.getPosY(), plotCheck_6.getWorld(), QueueTaskType.CREATE_PLOT_BORDER);
			String[] args_6 = { String.format("%d", plotCheck_6.getPosX()), String.format("%d", plotCheck_6.getPosY()) };
			executingPlayer.sendLocalizedString("%plotAutoClaimed%", args_6);
			// #5: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "home":
			// SIZE 0
			// PLOT CHECK
			PlotCheck plotCheck_7 = new PlotCheck(plugin, null);
			if (!plotCheck_7.simpleCheckForCorrectWorld()) {
				String[] args_7 = { plugin.config_world };
				executingPlayer.sendLocalizedString("%worldNotFound%", args_7);
				break;
			}
			// TESTS
			if (executingPlayer.getPlots().size() == 0) {
				executingPlayer.sendLocalizedString("%noPlotClaimed%", null);
				break;
			}
			// EXECUTE COMMAND
			// #1: Calculate location
			int portX_7 = executingPlayer.getPlots().get(0).getPlaceX() * plugin.config_jumpInterval;
			int portY_7 = executingPlayer.getPlots().get(0).getPlaceY() * plugin.config_jumpInterval;
			Location portLocation_7 = new Location(plotCheck_7.getWorld(), portX_7, (plugin.config_levelHeight + 1), portY_7);
			portLocation_7.setYaw(-45F);
			// #2: Kick off
			RunnableTeleport teleport_7 = new RunnableTeleport(plugin, player, portLocation_7, true);
			teleport_7.run();
			break;
		case "delete":
			// SIZE 1
			// Security Check
			if (!securityCheck(executingPlayer, args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_8 = new PlotCheck(plugin, executingPlayer);
			if (!plotCheck_8.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_8.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_8.checkForNotFreePlot()) {
				break;
			}
			// EXECUTE COMMAND
			// #1: Find plot
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_8.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_8.getPosY())) {
						// #2: Remove WG region
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_8.getWorld());
						// #3: Remove Plot and save all plots
						plugin.flatMePlayers.getPlayer(i).getPlots().remove(j);
						plugin.configurator.saveAllPlots();
						// #4: Add tasks
						executingPlayer.getQueue().addTask(plotCheck_8.getPosX(), plotCheck_8.getPosY(), plotCheck_8.getWorld(), QueueTaskType.CREATE_PLOT_BORDER);
					}
				}
			}
			executingPlayer.sendLocalizedString("%plotDeleted%", null);
			// #5: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "add":
			// SIZE 1
			// PLOT CHECK
			PlotCheck plotCheck_9 = new PlotCheck(plugin, executingPlayer);
			if (!plotCheck_9.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_9.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_9.checkForNotFreePlot()) {
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
					executingPlayer.sendLocalizedString("%playerNotFound%", args_9);
					break;
				} else {
					uuid_9_1 = essentialsUser.getConfigUUID();
				}
			} else {
				uuid_9_1 = addPlayer.getUniqueId();
			}
			// EXECUTE COMMAND
			// #1: Add member to all players
			plugin.flatMePlayers.add(uuid_9_1);
			// #2: Find plot
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_9.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_9.getPosY())) {
						// #3: Already member?
						if (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getMembers().getPlayer(uuid_9_1) != null) {
							String[] args_9 = { args[1] };
							executingPlayer.sendLocalizedString("%playerAlreadyMember%", args_9);
						} else {
							// #4: Add member and save all plots
							plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getMembers().add(uuid_9_1);
							plugin.configurator.saveAllPlots();
							// #5: Remove WG region
							plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_9.getWorld());
							// #6: Create WG region
							plugin.flatMePlayers.getPlayer(i).getPlots().get(j).createWGRegion(plotCheck_9.getWorld());
							String[] args_9 = { args[1] };
							executingPlayer.sendLocalizedString("%playerAdded%", args_9);
						}
					}
				}
			}
			break;
		case "remove":
			// SIZE 1
			// PLOT CHECK
			PlotCheck plotCheck_10 = new PlotCheck(plugin, executingPlayer);
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
					executingPlayer.sendLocalizedString("%playerNotFound%", args_9);
					break;
				} else {
					uuid_10_1 = essentialsUser.getConfigUUID();
				}
			} else {
				uuid_10_1 = removePlayer.getUniqueId();
			}
			// EXECUTE COMMAND
			// #1: Add member to all players
			plugin.flatMePlayers.add(uuid_10_1);
			// #2: Find plot
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_10.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_10.getPosY())) {
						// #3: No member?
						if (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getMembers().getPlayer(uuid_10_1) == null) {
							String[] args_10 = { args[1] };
							executingPlayer.sendLocalizedString("%playerNotAMember%", args_10);
						} else {
							// #4: Remove member and save all plots
							plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getMembers().remove(uuid_10_1);
							plugin.configurator.saveAllPlots();
							// #5: Remove WG region
							plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_10.getWorld());
							// #6: Create WG region
							plugin.flatMePlayers.getPlayer(i).getPlots().get(j).createWGRegion(plotCheck_10.getWorld());
							String[] args_10 = { args[1] };
							executingPlayer.sendLocalizedString("%playerRemoved%", args_10);
						}
					}
				}
			}
			break;
		case "extend":
			// SIZE 1
			// Security Check
			if (!securityCheck(executingPlayer, args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_11 = new PlotCheck(plugin, executingPlayer);
			if (!plotCheck_11.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_11.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_11.checkForNotFreePlot()) {
				break;
			}
			if (!plotCheck_11.checkForRightOwner()) {
				break;
			}
			// TESTS
			Double costs = Double.parseDouble(Integer.toString(plugin.config_extendCost));
			Double balance = 0D;
			try {
				balance = Economy.getMoney(executingPlayer.getDisplayName());
			} catch (Exception e0) {
				e0.printStackTrace();
			}
			if (balance < costs) {
				executingPlayer.sendLocalizedString("%notEnoughMoney%", null);
				break;
			}
			// EXECUTE COMMAND
			// #1: Enough money?
			try {
				Economy.subtract(executingPlayer.getDisplayName(), costs);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			// #2: New expire date
			Long newExpire_11 = System.currentTimeMillis() + (plugin.config_daysPerPlot * 86400000L);
			// #3: Find plot
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_11.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_11.getPosY())) {
						// #4: Apply new expire date and save all plots
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).setExpireDate(newExpire_11);
						plugin.configurator.saveAllPlots();
						// #5: Add tasks
						executingPlayer.getQueue().addTask(plotCheck_11.getPosX(), plotCheck_11.getPosY(), plotCheck_11.getWorld(), QueueTaskType.CREATE_PLOT_BORDER);
						String[] args_11 = { plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableExpireDate() };
						executingPlayer.sendLocalizedString("%plotExtended%", args_11);
					}
				}
			}
			// #6: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "lock":
			// SIZE 1
			// PLOT CHECK
			PlotCheck plotCheck_12 = new PlotCheck(plugin, executingPlayer);
			if (!plotCheck_12.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_12.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_12.checkForNotFreePlot()) {
				break;
			}
			// EXECUTE COMMAND
			// #1: Find plot
			boolean nowLocked = false;
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_12.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_12.getPosY())) {
						// #2: Toggle locked status and save all plots
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).toggleLocked();
						nowLocked = plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isLocked();
						plugin.configurator.saveAllPlots();
						// #3: Add tasks
						executingPlayer.getQueue().addTask(plotCheck_12.getPosX(), plotCheck_12.getPosY(), plotCheck_12.getWorld(), QueueTaskType.CREATE_PLOT_BORDER);
					}
				}
			}
			if (nowLocked) {
				executingPlayer.sendLocalizedString("%plotLockedTrue%", null);
			} else {
				executingPlayer.sendLocalizedString("%plotLockedFalse%", null);
			}
			// #4: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "info":
			// SIZE 0
			// PLOT CHECK
			PlotCheck plotCheck_13 = new PlotCheck(plugin, executingPlayer);
			if (!plotCheck_13.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_13.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_13.checkForNotFreePlot()) {
				break;
			}
			// EXECUTE COMMAND
			// #1: Find plot
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_13.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_13.getPosY())) {
						// #2: Send informations
						executingPlayer.sendLocalizedString("%infoHeader%", null);
						String[] args_13 = { String.format("%d", plotCheck_13.getPosX()), String.format("%d", plotCheck_13.getPosY()) };
						executingPlayer.sendLocalizedString("%infoID%", args_13);
						String[] args_13_1 = { plugin.flatMePlayers.getPlayer(i).getDisplayName() };
						executingPlayer.sendLocalizedString("%infoOwner%", args_13_1);
						String[] args_13_2 = { plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableMemberList() };
						executingPlayer.sendLocalizedString("%infoMembers%", args_13_2);
						String arg_13_3 = "";
						if (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired()) {
							arg_13_3 = ChatColor.DARK_RED + plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableExpireDate();
						} else {
							arg_13_3 = ChatColor.DARK_GREEN + plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableExpireDate();
						}
						String[] args_13_3 = { arg_13_3 };
						executingPlayer.sendLocalizedString("%infoExpire%", args_13_3);
						String arg_13_4 = "";
						if (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isLocked()) {
							arg_13_4 = ChatColor.DARK_GREEN + plugin.configurator.resolveLocalizedString("%answerYes%", null);
						} else {
							arg_13_4 = ChatColor.DARK_RED + plugin.configurator.resolveLocalizedString("%answerNo%", null);
						}
						String[] args_13_4 = { arg_13_4 };
						executingPlayer.sendLocalizedString("%infoLocked%", args_13_4);
					}
				}
			}
			break;
		case "check":
			// SIZE 0
			// EXECUTE COMMAND
			// #1: Parse page number
			int cmdPage_14 = 1;
			if (args.length > 1) {
				cmdPage_14 = parsePageNumber(args[1]);
			}
			// #2: Find plots
			List<String> expiredPlots = new ArrayList<String>();
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired()) && (!plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isLocked())) {
						// #3: Add plot to table
						expiredPlots.add("Plot " + String.format("%d", plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX()) + ","
								+ String.format("%d", plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY()) + " (" + plugin.flatMePlayers.getPlayer(i).getDisplayName() + ") - "
								+ plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableExpireDate());
					}
				}
			}
			// #4: Show table
			showTable(sender, plugin.configurator.resolveLocalizedString("%expiredPlots%", null), cmdPage_14, expiredPlots);
			break;
		case "clean":
			// SIZE 2
			// Security Check
			if (!securityCheck(executingPlayer, args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_15 = new PlotCheck(plugin, null);
			if (!plotCheck_15.simpleCheckForCorrectWorld()) {
				String[] args_15 = { plugin.config_world };
				executingPlayer.sendLocalizedString("%worldNotFound%", args_15);
				break;
			}
			// TESTS
			int cmdCount_15 = 1;
			if (args.length > 1) {
				cmdCount_15 = parseCount(args[1]);
			}
			if (cmdCount_15 < 1) {
				executingPlayer.sendLocalizedString(returnCorrectUsage("clean"), null);
				break;
			}
			// EXECUTE COMMAND
			// #1: Find plots
			executingPlayer.sendLocalizedString("%commandMayTakeAWhile%", null);
			int toDo_15 = 0;
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				if (toDo_15 >= cmdCount_15) {
					break;
				}
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if (toDo_15 >= cmdCount_15) {
						break;
					}
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired()) && (!plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isLocked())) {
						// #2: Remove WG region
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_15.getWorld());
						// #3: Add tasks
						executingPlayer.getQueue().addTask(plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX(), plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY(),
								plotCheck_15.getWorld(), QueueTaskType.CLEAN_MESSAGE);
						executingPlayer.getQueue().addTask(plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX(), plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY(),
								plotCheck_15.getWorld(), QueueTaskType.CREATE_PLOT_BORDER);
						executingPlayer.getQueue().addTask(plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX(), plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY(),
								plotCheck_15.getWorld(), QueueTaskType.REGEN_PLOT);
						executingPlayer.getQueue().addTask(plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX(), plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY(),
								plotCheck_15.getWorld(), QueueTaskType.ENTITY_REMOVE);
						// #4: Remove plot
						plugin.flatMePlayers.getPlayer(i).getPlots().remove(j);
						toDo_15++;
					}
				}
			}
			// #5: Save all plots
			plugin.configurator.saveAllPlots();
			executingPlayer.sendLocalizedString("%commandHasBeenQueued%", null);
			String[] args_15 = { String.format("%d", toDo_15) };
			executingPlayer.sendLocalizedString("%plotsCleaned%", args_15);
			// #6: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "regen":
			// SIZE 1
			// Security Check
			if (!securityCheck(executingPlayer, args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_16 = new PlotCheck(plugin, executingPlayer);
			if (!plotCheck_16.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_16.checkForPlotInArea()) {
				break;
			}
			// EXECUTE COMMAND
			// #1: Add tasks
			executingPlayer.getQueue().addTask(plotCheck_16.getPosX(), plotCheck_16.getPosY(), plotCheck_16.getWorld(), QueueTaskType.REGEN_PLOT);
			executingPlayer.getQueue().addTask(plotCheck_16.getPosX(), plotCheck_16.getPosY(), plotCheck_16.getWorld(), QueueTaskType.ENTITY_REMOVE);
			executingPlayer.sendLocalizedString("%plotRegenerated%", null);
			// #2: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "weclean":
			// SIZE 2
			// Security Check
			if (!securityCheck(executingPlayer, args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_17 = new PlotCheck(plugin, null);
			if (!plotCheck_17.simpleCheckForCorrectWorld()) {
				String[] args_17 = { plugin.config_world };
				executingPlayer.sendLocalizedString("%worldNotFound%", args_17);
				break;
			}
			// TESTS
			int cmdCount_17 = 1;
			if (args.length > 1) {
				cmdCount_17 = parseCount(args[1]);
			}
			if (cmdCount_17 < 1) {
				executingPlayer.sendLocalizedString(returnCorrectUsage("clean"), null);
				break;
			}
			// EXECUTE COMMAND
			// #1: Find plots
			executingPlayer.sendLocalizedString("%commandMayTakeAWhile%", null);
			int toDo_17 = 0;
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				if (toDo_17 >= cmdCount_17) {
					break;
				}
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if (toDo_17 >= cmdCount_17) {
						break;
					}
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired()) && (!plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isLocked())) {
						// #2: Remove WG region
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_17.getWorld());
						// #3: Add tasks
						executingPlayer.getQueue().addTask(plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX(), plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY(),
								plotCheck_17.getWorld(), QueueTaskType.CLEAN_MESSAGE);
						executingPlayer.getQueue().addTask(plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX(), plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY(),
								plotCheck_17.getWorld(), QueueTaskType.CREATE_PLOT_BORDER);
						executingPlayer.getQueue().addTask(plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX(), plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY(),
								plotCheck_17.getWorld(), QueueTaskType.WE_REGEN_PLOT);
						// #4: Remove plot
						plugin.flatMePlayers.getPlayer(i).getPlots().remove(j);
						toDo_17++;
					}
				}
			}
			// #5: Save all plots
			plugin.configurator.saveAllPlots();
			executingPlayer.sendLocalizedString("%commandHasBeenQueued%", null);
			String[] args_17 = { String.format("%d", toDo_17) };
			executingPlayer.sendLocalizedString("%plotsCleaned%", args_17);
			// #6: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "weregen":
			// SIZE 1
			// Security Check
			if (!securityCheck(executingPlayer, args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_18 = new PlotCheck(plugin, executingPlayer);
			if (!plotCheck_18.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_18.checkForPlotInArea()) {
				break;
			}
			// EXECUTE COMMAND
			// #1: Add tasks
			executingPlayer.getQueue().addTask(plotCheck_18.getPosX(), plotCheck_18.getPosY(), plotCheck_18.getWorld(), QueueTaskType.WE_REGEN_PLOT);
			executingPlayer.sendLocalizedString("%plotRegenerated%", null);
			// #2: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "teleport":
			// SIZE 0
			// TESTS
			int cmdPosX_19 = 0;
			cmdPosX_19 = parsePosition(args[1]);
			int cmdPosY_19 = 0;
			cmdPosY_19 = parsePosition(args[2]);
			// PLOT CHECK
			PlotCheck plotCheck_19 = new PlotCheck(plugin, null);
			if (!plotCheck_19.simpleCheckForCorrectWorld()) {
				String[] args_19 = { plugin.config_world };
				executingPlayer.sendLocalizedString("%worldNotFound%", args_19);
				break;
			}
			if (!plotCheck_19.checkForPlotInArea(cmdPosX_19, cmdPosY_19)) {
				executingPlayer.sendLocalizedString("%wouldBeOutOfArea%", null);
				break;
			}
			// EXECUTE COMMAND
			// #1: Calculate location
			int portX_19 = cmdPosX_19 * plugin.config_jumpInterval;
			int portY_19 = cmdPosY_19 * plugin.config_jumpInterval;
			Location portLocation_19 = new Location(plotCheck_19.getWorld(), portX_19, (plugin.config_levelHeight + 1), portY_19);
			portLocation_19.setYaw(-45F);
			// #2: Kick off
			RunnableTeleport teleport_19 = new RunnableTeleport(plugin, player, portLocation_19, false);
			teleport_19.run();
			break;
		case "hide":
			// SIZE 0
			// EXECUTE COMMAND
			// #1: Hide queue
			executingPlayer.sendLocalizedString("%queueHidden%", null);
			executingPlayer.setQueueSilence(true);
			break;
		case "show":
			// SIZE 0
			// EXECUTE COMMAND
			// #1: Show queue
			executingPlayer.sendLocalizedString("%queueShown%", null);
			executingPlayer.setQueueSilence(false);
			break;
		case "repair":
			// SIZE 1
			// TESTS
			int cmdPosX_21 = 0;
			cmdPosX_21 = parsePosition(args[1]);
			int cmdPosY_21 = 0;
			cmdPosY_21 = parsePosition(args[2]);
			// PLOT CHECK
			PlotCheck plotCheck_21 = new PlotCheck(plugin, null);
			if (!plotCheck_21.simpleCheckForCorrectWorld()) {
				String[] args_21 = { plugin.config_world };
				executingPlayer.sendLocalizedString("%worldNotFound%", args_21);
				break;
			}
			if (!plotCheck_21.checkForPlotInArea(cmdPosX_21, cmdPosY_21)) {
				executingPlayer.sendLocalizedString("%wouldBeOutOfArea%", null);
				break;
			}
			// EXECUTE COMMAND
			// #1: Add tasks
			executingPlayer.getQueue().addTask(cmdPosX_21, cmdPosY_21, plotCheck_21.getWorld(), QueueTaskType.CREATE_RUNWAY);
			executingPlayer.sendLocalizedString("%pathRepaired%", null);
			// #2: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "move":
			// SIZE 0
			// Security Check
			if (!securityCheck(executingPlayer, args)) {
				break;
			}
			// TESTS
			int cmdPosX_22 = 0;
			cmdPosX_22 = parsePosition(args[1]);
			int cmdPosY_22 = 0;
			cmdPosY_22 = parsePosition(args[2]);
			// PLOT CHECK
			PlotCheck plotCheck_22 = new PlotCheck(plugin, executingPlayer);
			if (!plotCheck_22.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_22.checkForPlotInArea(cmdPosX_22, cmdPosY_22)) {
				executingPlayer.sendLocalizedString("%wouldBeOutOfArea%", null);
				break;
			}
			if (!plotCheck_22.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_22.checkForNotFreePlot()) {
				break;
			}
			if (!plotCheck_22.checkForFreePlot(cmdPosX_22, cmdPosY_22)) {
				executingPlayer.sendLocalizedString("%plotAlreadyOwned%", null);
				break;
			}
			plugin.getLogger().info(plotCheck_22.getPosX() + "," + plotCheck_22.getPosY());
			// EXECUTE COMMAND
			// #1: Find plot
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_22.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_22.getPosY())) {
						// #2: Remove WG region
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_22.getWorld());
						// #3: Change id
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).setPlaceX(cmdPosX_22);
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).setPlaceY(cmdPosY_22);
						// #4: Create WG region
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).createWGRegion(plotCheck_22.getWorld());
						// #5: Save all plots
						plugin.configurator.saveAllPlots();
						// #6: RUNNABLE_MOVE
						RunnableMove plotMove = new RunnableMove(plugin, player, plotCheck_22.getPosX(), plotCheck_22.getPosY(), cmdPosX_22, cmdPosY_22, plotCheck_22.getWorld());
						plotMove.run();
						// #7: Add tasks
						executingPlayer.getQueue().addTask(plotCheck_22.getPosX(), plotCheck_22.getPosY(), plotCheck_22.getWorld(), QueueTaskType.CREATE_PLOT_BORDER);
						executingPlayer.getQueue().addTask(cmdPosX_22, cmdPosY_22, plotCheck_22.getWorld(), QueueTaskType.CREATE_PLOT_BORDER);
					}
				}
			}
			executingPlayer.sendLocalizedString("%plotMoved%", null);
			// #8: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "update":
			// SIZE 1
			// PLOT CHECK
			PlotCheck plotCheck_23 = new PlotCheck(plugin, executingPlayer);
			if (!plotCheck_23.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_23.checkForPlotInArea()) {
				break;
			}
			if (!plotCheck_23.checkForRightOwner()) {
				break;
			}
			// EXECUTE COMMAND
			// #1: Find plot
			boolean foundAOwnedPlot = false;
			for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
				for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
					if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == plotCheck_23.getPosX())
							&& (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == plotCheck_23.getPosY())) {
						foundAOwnedPlot = true;
						// #2: Remove WG region
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).deleteWGRegion(plotCheck_23.getWorld());
						// #3: Create WG region
						plugin.flatMePlayers.getPlayer(i).getPlots().get(j).createWGRegion(plotCheck_23.getWorld());
						// #4: Add tasks
						executingPlayer.getQueue().addTask(plotCheck_23.getPosX(), plotCheck_23.getPosY(), plotCheck_23.getWorld(), QueueTaskType.CREATE_PLOT_BORDER);
					}
				}
			}
			if (!foundAOwnedPlot) {
				executingPlayer.getQueue().addTask(plotCheck_23.getPosX(), plotCheck_23.getPosY(), plotCheck_23.getWorld(), QueueTaskType.CREATE_PLOT_BORDER);
			}
			executingPlayer.sendLocalizedString("%plotUpdated%", null);
			// #5: Kick off
			executingPlayer.getQueue().runTaskQueue();
			break;
		case "tool":
			// SIZE 0
			// EXECUTE COMMAND
			// #1: Give a tool book
			Bukkit.getServer()
					.dispatchCommand(
							Bukkit.getServer().getConsoleSender(),
							"give "
									+ executingPlayer.getDisplayName()
									+ " written_book 1 0 {pages:[\"[\\\"\\\",{text:\\\"== FlatMe Tool Book ==\\\",color:gold},{text:\\\"\\n\\n\\\",color:reset},{text:Informations,color:dark_blue,clickEvent:{action:run_command,value:\\\"/flatme info\\\"}},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Delete this plot\\\",color:dark_red,clickEvent:{action:run_command,value:\\\"/flatme delete\\\"}},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Regen with Flatme\\\",color:dark_aqua,clickEvent:{action:run_command,value:\\\"/flatme regen\\\"}},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Regen with WorldEdit\\\",color:dark_aqua,clickEvent:{action:run_command,value:\\\"/flatme weregen\\\"}},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Apply task (YES!)\\\",color:dark_green,clickEvent:{action:run_command,value:\\\"/flatme yes\\\"}}]\",\"[\\\"\\\",{text:\\\"== FlatMe Tool Book ==\\\",color:gold},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Extend this plot\\\",color:dark_purple,clickEvent:{action:run_command,value:\\\"/flatme extend\\\"}},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Lock / Unlock\\\",color:dark_purple,clickEvent:{action:run_command,value:\\\"/flatme lock\\\"}},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Update this plot\\\",color:dark_purple,clickEvent:{action:run_command,value:\\\"/flatme update\\\"}},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Apply task (YES!)\\\",color:dark_green,clickEvent:{action:run_command,value:\\\"/flatme yes\\\"}},{text:\\\"\\n \\\",color:reset}]\",\"[\\\"\\\",{text:\\\"== FlatMe Tool Book ==\\\",color:gold},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Claim this plot\\\",color:dark_purple,clickEvent:{action:run_command,value:\\\"/flatme claim\\\"}},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Autoclaim a plot\\\",color:dark_purple,clickEvent:{action:run_command,value:\\\"/flatme autoclaim\\\"}},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Teleport to first plot\\\",color:dark_purple,clickEvent:{action:run_command,value:\\\"/flatme home\\\"}},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Apply task (YES!)\\\",color:dark_green,clickEvent:{action:run_command,value:\\\"/flatme yes\\\"}}]\",\"[\\\"\\\",{text:\\\"== FlatMe Tool Book ==\\\",color:gold},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Create plotarea\\\",color:dark_purple,clickEvent:{action:run_command,value:\\\"/flatme create\\\"}},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Update all plots\\\",color:dark_purple,clickEvent:{action:run_command,value:\\\"/flatme updateall\\\"}},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Reload configuration\\\",color:dark_purple,clickEvent:{action:run_command,value:\\\"/flatme reload\\\"}},{text:\\\"\\n\\n\\\",color:reset},{text:\\\"Apply task (YES!)\\\",color:dark_green,clickEvent:{action:run_command,value:\\\"/flatme yes\\\"}}]\"],title:\""
									+ ChatColor.GOLD + "FlatMe Tool Book" + ChatColor.RESET + "\",author:FlatMe}");
			executingPlayer.sendLocalizedString("%receivedTool%", null);
			break;
		default:
			// SIZE 0
			// EXECUTE COMMAND
			// #1: Return error
			String[] args_x = { firstArg };
			executingPlayer.sendLocalizedString("%commandError%", args_x);
			executingPlayer.sendLocalizedString(returnCorrectUsage("help"), null);
			break;
		}
	}

	public boolean securityCheck(FlatMePlayer player, String[] args) {
		if ((player.getSecurityCommand() == null) || (!player.isAnsweredYes())) {
			String[] warning = { args[0] };
			for (int i = 1; i < args.length; i++) {
				warning[0] = warning[0] + " " + args[i];
			}
			player.sendLocalizedString("%securityQuestion%", warning);
			player.setSecurityCommand(args);
			return false;
		} else {
			player.setAnsweredYes(false);
			player.setSecurityCommand(null);
			return true;
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
			if (!((sender.hasPermission(commandList.getCommand(i).getPermission())) || (sender.hasPermission("flatme.command." + commandList.getCommandText(i))))) {
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

	public boolean isFreePlot(int placeX, int placeY) {
		for (int i = 0; i < plugin.flatMePlayers.size(); i++) {
			for (int j = 0; j < plugin.flatMePlayers.getPlayer(i).getPlots().size(); j++) {
				if ((plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceX() == placeX) && (plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getPlaceY() == placeY)) {
					return false;
				}
			}
		}
		return true;
	}

	public Coordinates nextEmptyPlot() {
		Coordinates tempCoords = null;
		Double bestDiff = 10000D;
		for (int i = -plugin.config_radius; i < plugin.config_radius; i++) {
			for (int j = -plugin.config_radius; j < plugin.config_radius; j++) {
				if (isFreePlot(i, j)) {
					Double centerBlockX = (i * plugin.config_jumpInterval) + (plugin.config_jumpInterval / 2D);
					Double centerBlockY = (j * plugin.config_jumpInterval) + (plugin.config_jumpInterval / 2D);
					Double thisDiff = Math.sqrt((centerBlockX * centerBlockX) + (centerBlockY * centerBlockY));
					if (thisDiff <= bestDiff) {
						if (tempCoords == null) {
							tempCoords = new Coordinates();
						}
						bestDiff = thisDiff;
						tempCoords.setSimpleCoordX(i);
						tempCoords.setSimpleCoordY(j);
					}
				}
			}
		}
		return tempCoords;
	}

	public Coordinates getStanding(Player player) {
		Location loc = player.getLocation();
		Coordinates tempCoords = new Coordinates();
		double valueX = loc.getX() / plugin.config_jumpInterval;
		double valueY = loc.getZ() / plugin.config_jumpInterval;
		tempCoords.setSimpleCoordX((int) Math.floor(valueX));
		tempCoords.setSimpleCoordY((int) Math.floor(valueY));
		return tempCoords;
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
		commandList.add("hide", new Command("flatme.admin", "/flatme hide", 0));
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
		commandList.add("tool", new Command("flatme.admin", "/flatme tool", 0));
		commandList.add("updateall", new Command("flatme.admin", "/flatme updateall", 0));
		commandList.add("update", new Command("flatme.player", "/flatme update", 0));
		commandList.add("version", new Command("flatme.player", "/flatme version", 0));
		commandList.add("weclean", new Command("flatme.admin", "/flatme weclean <count>", 1));
		commandList.add("weregen", new Command("flatme.admin", "/flatme weregen", 0));
		commandList.add("yes", new Command("flatme.player", "/flatme yes", 0));
	}

	private void sendLocalizedString(CommandSender sender, String input, String[] args) {
		sender.sendMessage(plugin.configurator.resolveLocalizedString(input, args));
	}

	private void sendConsoleLocalizedString(CommandSender sender, String input, String[] args) {
		plugin.getLogger().info(plugin.configurator.resolveLocalizedString(input, args));
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
