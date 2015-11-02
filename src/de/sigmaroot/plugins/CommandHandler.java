package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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

	@SuppressWarnings("deprecation")
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
		if (!sender.hasPermission(executedCommand.getPermission())) {
			sendLocalizedString(sender, "%noPermission%", null);
			return;
		}
		switch (firstArg) {
		case "yes":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player = (Player) sender;
			UUID uuid = player.getUniqueId();
			plugin.flatMePlayers.add(uuid);
			// Run saved command
			if (plugin.flatMePlayers.getPlayer(uuid).getSecurityCommand() != null) {
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
			sendLocalizedString(sender, "%pluginVersion%", args_1);
			break;
		case "create":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_2 = (Player) sender;
			UUID uuid_2 = player_2.getUniqueId();
			plugin.flatMePlayers.add(uuid_2);
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid_2), args)) {
				break;
			}
			// TESTS
			World world_2 = Bukkit.getWorld(plugin.config_world);
			if (world_2 == null) {
				String[] args_2 = { plugin.config_world };
				plugin.flatMePlayers.getPlayer(uuid_2).sendLocalizedString("%worldNotFound%", args_2);
				break;
			}
			if (plugin.config_radius < 1) {
				plugin.flatMePlayers.getPlayer(uuid_2).sendLocalizedString("%tooSmallRadius%", null);
				break;
			}
			// Execute command
			BlockChanger blockChanger_2 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid_2), world_2);
			blockChanger_2.runCreate();
			PlayerQueue playerQueue_2 = plugin.flatMePlayers.getPlayer(uuid_2).getQueue();
			playerQueue_2.run();
			break;
		case "reload":
			// Execute command
			sendLocalizedString(sender, "%reloadingConfiguration%", null);
			plugin.reloadConfiguration();
			sendLocalizedString(sender, "%reloadedSuccessfully%", null);
			break;
		case "update":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_4 = (Player) sender;
			UUID uuid_4 = player_4.getUniqueId();
			plugin.flatMePlayers.add(uuid_4);
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid_4), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_4 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_4));
			if (!plotCheck_4.checkForCorrectWorld()) {
				break;
			}
			// Execute command
			sendLocalizedString(sender, "%updatingPlots%", null);
			int removedRegions = plugin.worldGuardHandler.removeAllRegions(plotCheck_4.getWorld());
			String[] args_4 = { String.format("%d", removedRegions) };
			plugin.flatMePlayers.getPlayer(uuid_4).sendLocalizedString("%removedRegions%", args_4);
			int createdRegions = plugin.worldGuardHandler.createAllRegions(plotCheck_4.getWorld());
			String[] args_4_1 = { String.format("%d", createdRegions) };
			plugin.flatMePlayers.getPlayer(uuid_4).sendLocalizedString("%createdRegions%", args_4_1);
			plugin.flatMePlayers.getPlayer(uuid_4).sendLocalizedString("%commandMayTakeAWhile%", null);
			BlockChanger blockChanger_4 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid_4), plotCheck_4.getWorld());
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
			plugin.flatMePlayers.getPlayer(uuid_4).sendLocalizedString("%plotsUpdated%", null);
			plugin.flatMePlayers.getPlayer(uuid_4).sendLocalizedString("%commandHasBeenQueued%", null);
			PlayerQueue playerQueue_4 = plugin.flatMePlayers.getPlayer(uuid_4).getQueue();
			playerQueue_4.run();
			break;
		case "claim":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_5 = (Player) sender;
			UUID uuid_5 = player_5.getUniqueId();
			plugin.flatMePlayers.add(uuid_5);
			// PLOT CHECK
			PlotCheck plotCheck_5 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_5));
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
			if (plugin.flatMePlayers.getPlayer(uuid_5).getPlots().size() >= plugin.config_maxPlots) {
				if (!player_5.hasPermission("flatme.moreplots")) {
					plugin.flatMePlayers.getPlayer(uuid_5).sendLocalizedString("%tooManyPlots%", null);
					break;
				}
			}
			// Execute command
			PlayerMap emptyMembers_5 = new PlayerMap(plugin);
			Long newExpire_5 = System.currentTimeMillis() + (plugin.config_daysPerPlot * 86400000L);
			Plot tempPlot_5 = new Plot(plugin, plotCheck_5.getPosX(), plotCheck_5.getPosY(), plugin.flatMePlayers.getPlayer(uuid_5), emptyMembers_5, newExpire_5, false);
			tempPlot_5.createWGRegion(plotCheck_5.getWorld());
			plugin.flatMePlayers.getPlayer(uuid_5).getPlots().add(tempPlot_5);
			plugin.configurator.saveAllPlots();
			BlockChanger blockChanger_5 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid_5), plotCheck_5.getWorld());
			blockChanger_5.runPlot(plotCheck_5.getPosX(), plotCheck_5.getPosY(), true, false, false);
			plugin.flatMePlayers.getPlayer(uuid_5).setQueueSilence(true);
			PlayerQueue playerQueue_5 = plugin.flatMePlayers.getPlayer(uuid_5).getQueue();
			playerQueue_5.run();
			plugin.flatMePlayers.getPlayer(uuid_5).sendLocalizedString("%plotClaimed%", null);
			break;
		case "autoclaim":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_6 = (Player) sender;
			UUID uuid_6 = player_6.getUniqueId();
			plugin.flatMePlayers.add(uuid_6);
			// PLOT CHECK
			PlotCheck plotCheck_6 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_6));
			if (!plotCheck_6.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_6.checkForNextPlot()) {
				break;
			}
			// TESTS
			if (plugin.flatMePlayers.getPlayer(uuid_6).getPlots().size() >= plugin.config_maxPlots) {
				if (!player_6.hasPermission("flatme.moreplots")) {
					plugin.flatMePlayers.getPlayer(uuid_6).sendLocalizedString("%tooManyPlots%", null);
					break;
				}
			}
			// Execute command
			PlayerMap emptyMembers_6 = new PlayerMap(plugin);
			Long newExpire_6 = System.currentTimeMillis() + (plugin.config_daysPerPlot * 86400000L);
			Plot tempPlot_6 = new Plot(plugin, plotCheck_6.getPosX(), plotCheck_6.getPosY(), plugin.flatMePlayers.getPlayer(uuid_6), emptyMembers_6, newExpire_6, false);
			tempPlot_6.createWGRegion(plotCheck_6.getWorld());
			plugin.flatMePlayers.getPlayer(uuid_6).getPlots().add(tempPlot_6);
			plugin.configurator.saveAllPlots();
			BlockChanger blockChanger_6 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid_6), plotCheck_6.getWorld());
			blockChanger_6.runPlot(plotCheck_6.getPosX(), plotCheck_6.getPosY(), true, false, false);
			plugin.flatMePlayers.getPlayer(uuid_6).setQueueSilence(true);
			PlayerQueue playerQueue_6 = plugin.flatMePlayers.getPlayer(uuid_6).getQueue();
			playerQueue_6.run();
			String[] args_6 = { String.format("%d", plotCheck_6.getPosX()), String.format("%d", plotCheck_6.getPosY()) };
			plugin.flatMePlayers.getPlayer(uuid_6).sendLocalizedString("%plotAutoClaimed%", args_6);
			break;
		case "home":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_7 = (Player) sender;
			UUID uuid_7 = player_7.getUniqueId();
			plugin.flatMePlayers.add(uuid_7);
			// TESTS
			World world_7 = Bukkit.getWorld(plugin.config_world);
			if (world_7 == null) {
				String[] args_7 = { plugin.config_world };
				sendLocalizedString(sender, "%worldNotFound%", args_7);
				break;
			}
			if (plugin.flatMePlayers.getPlayer(uuid_7).getPlots().size() == 0) {
				plugin.flatMePlayers.getPlayer(uuid_7).sendLocalizedString("%noPlotClaimed%", null);
				break;
			}
			// Execute command
			int portX_7 = plugin.flatMePlayers.getPlayer(uuid_7).getPlots().get(0).getPlaceX() * plugin.config_jumpInterval;
			int portY_7 = plugin.flatMePlayers.getPlayer(uuid_7).getPlots().get(0).getPlaceY() * plugin.config_jumpInterval;
			Location portLocation_7 = new Location(world_7, portX_7, (plugin.config_lvlHeight + 1), portY_7);
			portLocation_7.setYaw(-45F);
			player_7.teleport(portLocation_7);
			plugin.flatMePlayers.getPlayer(uuid_7).sendLocalizedString("%teleportToFirstPlot%", null);
			break;
		case "delete":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_8 = (Player) sender;
			UUID uuid_8 = player_8.getUniqueId();
			plugin.flatMePlayers.add(uuid_8);
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid_8), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_8 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_8));
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
						BlockChanger blockChanger_8 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid_8), plotCheck_8.getWorld());
						blockChanger_8.runPlot(plotCheck_8.getPosX(), plotCheck_8.getPosY(), false, false, false);
						plugin.flatMePlayers.getPlayer(uuid_8).setQueueSilence(true);
						PlayerQueue playerQueue_8 = plugin.flatMePlayers.getPlayer(uuid_8).getQueue();
						playerQueue_8.run();
					}
				}
			}
			plugin.flatMePlayers.getPlayer(uuid_8).sendLocalizedString("%plotDeleted%", null);
			break;
		case "add":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_9 = (Player) sender;
			UUID uuid_9 = player_9.getUniqueId();
			plugin.flatMePlayers.add(uuid_9);
			// PLOT CHECK
			PlotCheck plotCheck_9 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_9));
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
					plugin.flatMePlayers.getPlayer(uuid_9).sendLocalizedString("%playerNotFound%", args_9);
					break;
				} else {
					uuid_9_1 = essentialsUser.getConfigUUID();
				}
			} else {
				uuid_9_1 = addPlayer.getUniqueId();
			}
			// Execute command
			plugin.flatMePlayers.add(uuid_9_1);
			for (int i = 0; i < plugin.flatMePlayers.getPlayer(uuid_9).getPlots().size(); i++) {
				if ((plugin.flatMePlayers.getPlayer(uuid_9).getPlots().get(i).getPlaceX() == plotCheck_9.getPosX())
						&& (plugin.flatMePlayers.getPlayer(uuid_9).getPlots().get(i).getPlaceY() == plotCheck_9.getPosY())) {
					if (plugin.flatMePlayers.getPlayer(uuid_9).getPlots().get(i).getMembers().getPlayer(uuid_9_1) != null) {
						String[] args_9 = { args[1] };
						plugin.flatMePlayers.getPlayer(uuid_9).sendLocalizedString("%playerAlreadyMember%", args_9);
					} else {
						plugin.flatMePlayers.getPlayer(uuid_9).getPlots().get(i).getMembers().add(uuid_9_1);
						plugin.configurator.saveAllPlots();
						plugin.flatMePlayers.getPlayer(uuid_9).getPlots().get(i).deleteWGRegion(plotCheck_9.getWorld());
						plugin.flatMePlayers.getPlayer(uuid_9).getPlots().get(i).createWGRegion(plotCheck_9.getWorld());
						String[] args_9 = { args[1] };
						plugin.flatMePlayers.getPlayer(uuid_9).sendLocalizedString("%playerAdded%", args_9);
					}
				}
			}
			break;
		case "remove":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_10 = (Player) sender;
			UUID uuid_10 = player_10.getUniqueId();
			plugin.flatMePlayers.add(uuid_10);
			// PLOT CHECK
			PlotCheck plotCheck_10 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_10));
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
					plugin.flatMePlayers.getPlayer(uuid_10).sendLocalizedString("%playerNotFound%", args_9);
					break;
				} else {
					uuid_10_1 = essentialsUser.getConfigUUID();
				}
			} else {
				uuid_10_1 = removePlayer.getUniqueId();
			}
			// Execute command
			plugin.flatMePlayers.add(uuid_10_1);
			for (int i = 0; i < plugin.flatMePlayers.getPlayer(uuid_10).getPlots().size(); i++) {
				if ((plugin.flatMePlayers.getPlayer(uuid_10).getPlots().get(i).getPlaceX() == plotCheck_10.getPosX())
						&& (plugin.flatMePlayers.getPlayer(uuid_10).getPlots().get(i).getPlaceY() == plotCheck_10.getPosY())) {
					if (plugin.flatMePlayers.getPlayer(uuid_10).getPlots().get(i).getMembers().getPlayer(uuid_10_1) == null) {
						String[] args_10 = { args[1] };
						plugin.flatMePlayers.getPlayer(uuid_10).sendLocalizedString("%playerNotAMember%", args_10);
					} else {
						plugin.flatMePlayers.getPlayer(uuid_10).getPlots().get(i).getMembers().remove(uuid_10_1);
						plugin.configurator.saveAllPlots();
						plugin.flatMePlayers.getPlayer(uuid_10).getPlots().get(i).deleteWGRegion(plotCheck_10.getWorld());
						plugin.flatMePlayers.getPlayer(uuid_10).getPlots().get(i).createWGRegion(plotCheck_10.getWorld());
						String[] args_10 = { args[1] };
						plugin.flatMePlayers.getPlayer(uuid_10).sendLocalizedString("%playerRemoved%", args_10);
					}
				}
			}
			break;
		case "extend":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_11 = (Player) sender;
			UUID uuid_11 = player_11.getUniqueId();
			plugin.flatMePlayers.add(uuid_11);
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid_11), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_11 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_11));
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
				plugin.getLogger().info(plugin.flatMePlayers.getPlayer(uuid_11).getDisplayName());
				balance = Economy.getMoney(plugin.flatMePlayers.getPlayer(uuid_11).getDisplayName());
			} catch (Exception e0) {
				e0.printStackTrace();
			}
			if (balance < costs) {
				plugin.flatMePlayers.getPlayer(uuid_11).sendLocalizedString("%notEnoughMoney%", null);
				break;
			}
			// Execute command
			try {
				Economy.subtract(plugin.flatMePlayers.getPlayer(uuid_11).getDisplayName(), costs);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Long newExpire_11 = System.currentTimeMillis() + (plugin.config_daysPerPlot * 86400000L);
			for (int i = 0; i < plugin.flatMePlayers.getPlayer(uuid_11).getPlots().size(); i++) {
				if ((plugin.flatMePlayers.getPlayer(uuid_11).getPlots().get(i).getPlaceX() == plotCheck_11.getPosX())
						&& (plugin.flatMePlayers.getPlayer(uuid_11).getPlots().get(i).getPlaceY() == plotCheck_11.getPosY())) {
					plugin.flatMePlayers.getPlayer(uuid_11).getPlots().get(i).setExpireDate(newExpire_11);
					plugin.configurator.saveAllPlots();
					BlockChanger blockChanger_11 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid_11), plotCheck_11.getWorld());
					blockChanger_11.runPlot(plotCheck_11.getPosX(), plotCheck_11.getPosY(), true, plugin.flatMePlayers.getPlayer(uuid_11).getPlots().get(i).isExpired(), plugin.flatMePlayers
							.getPlayer(uuid_11).getPlots().get(i).isLocked());
					plugin.flatMePlayers.getPlayer(uuid_11).setQueueSilence(true);
					PlayerQueue playerQueue_11 = plugin.flatMePlayers.getPlayer(uuid_11).getQueue();
					playerQueue_11.run();
					String[] args_11 = { plugin.flatMePlayers.getPlayer(uuid_11).getPlots().get(i).getReadableExpireDate() };
					plugin.flatMePlayers.getPlayer(uuid_11).sendLocalizedString("%plotExtended%", args_11);
				}
			}
			break;
		case "lock":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_12 = (Player) sender;
			UUID uuid_12 = player_12.getUniqueId();
			plugin.flatMePlayers.add(uuid_12);
			// PLOT CHECK
			PlotCheck plotCheck_12 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_12));
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
						BlockChanger blockChanger_12 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid_12), plotCheck_12.getWorld());
						blockChanger_12.runPlot(plotCheck_12.getPosX(), plotCheck_12.getPosY(), true, plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isExpired(), plugin.flatMePlayers
								.getPlayer(i).getPlots().get(j).isLocked());
						plugin.flatMePlayers.getPlayer(uuid_12).setQueueSilence(true);
						PlayerQueue playerQueue_12 = plugin.flatMePlayers.getPlayer(uuid_12).getQueue();
						playerQueue_12.run();
					}
				}
			}
			if (nowLocked) {
				plugin.flatMePlayers.getPlayer(uuid_12).sendLocalizedString("%plotLockedTrue%", null);
			} else {
				plugin.flatMePlayers.getPlayer(uuid_12).sendLocalizedString("%plotLockedFalse%", null);
			}
			break;
		case "info":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_13 = (Player) sender;
			UUID uuid_13 = player_13.getUniqueId();
			plugin.flatMePlayers.add(uuid_13);
			// PLOT CHECK
			PlotCheck plotCheck_13 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_13));
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
						plugin.flatMePlayers.getPlayer(uuid_13).sendLocalizedString("%infoHeader%", null);
						String[] args_13 = { String.format("%d", plotCheck_13.getPosX()), String.format("%d", plotCheck_13.getPosY()) };
						plugin.flatMePlayers.getPlayer(uuid_13).sendLocalizedString("%infoID%", args_13);
						String[] args_13_1 = { plugin.flatMePlayers.getPlayer(i).getDisplayName() };
						plugin.flatMePlayers.getPlayer(uuid_13).sendLocalizedString("%infoOwner%", args_13_1);
						String[] args_13_2 = { plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableMemberList() };
						plugin.flatMePlayers.getPlayer(uuid_13).sendLocalizedString("%infoMembers%", args_13_2);
						String[] args_13_3 = { plugin.flatMePlayers.getPlayer(i).getPlots().get(j).getReadableExpireDate() };
						plugin.flatMePlayers.getPlayer(uuid_13).sendLocalizedString("%infoExpire%", args_13_3);
						String[] args_13_4 = { Boolean.toString(plugin.flatMePlayers.getPlayer(i).getPlots().get(j).isLocked()) };
						plugin.flatMePlayers.getPlayer(uuid_13).sendLocalizedString("%infoLocked%", args_13_4);
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
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_15 = (Player) sender;
			UUID uuid_15 = player_15.getUniqueId();
			plugin.flatMePlayers.add(uuid_15);
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid_15), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_15 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_15));
			if (!plotCheck_15.checkForCorrectWorld()) {
				break;
			}
			// TESTS
			int cmdCount_15 = 1;
			if (args.length > 1) {
				cmdCount_15 = parseCount(args[1]);
			}
			if (cmdCount_15 < 1) {
				sendLocalizedString(sender, returnCorrectUsage("clean"), null);
				break;
			}
			// Execute command
			plugin.flatMePlayers.getPlayer(uuid_15).sendLocalizedString("%commandMayTakeAWhile%", null);
			int toDo = 0;
			BlockChanger blockChanger_15 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid_15), plotCheck_15.getWorld());
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
			plugin.flatMePlayers.getPlayer(uuid_15).sendLocalizedString("%plotsCleaned%", args_15);
			plugin.flatMePlayers.getPlayer(uuid_15).sendLocalizedString("%commandHasBeenQueued%", null);
			PlayerQueue playerQueue_15 = plugin.flatMePlayers.getPlayer(uuid_15).getQueue();
			playerQueue_15.run();
			break;
		case "regen":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_16 = (Player) sender;
			UUID uuid_16 = player_16.getUniqueId();
			plugin.flatMePlayers.add(uuid_16);
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid_16), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_16 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_16));
			if (!plotCheck_16.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_16.checkForPlotInArea()) {
				break;
			}
			// Execute command
			plugin.flatMePlayers.getPlayer(uuid_16).sendLocalizedString("%commandMayTakeAWhile%", null);
			BlockChanger blockChanger_16 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid_16), plotCheck_16.getWorld());
			blockChanger_16.runRegen(plotCheck_16.getPosX(), plotCheck_16.getPosY());
			plugin.flatMePlayers.getPlayer(uuid_16).sendLocalizedString("%plotRegenerated%", null);
			plugin.flatMePlayers.getPlayer(uuid_16).sendLocalizedString("%commandHasBeenQueued%", null);
			PlayerQueue playerQueue_16 = plugin.flatMePlayers.getPlayer(uuid_16).getQueue();
			playerQueue_16.run();
			break;
		case "weregen":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_17 = (Player) sender;
			UUID uuid_17 = player_17.getUniqueId();
			plugin.flatMePlayers.add(uuid_17);
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid_17), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_17 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_17));
			if (!plotCheck_17.checkForCorrectWorld()) {
				break;
			}
			if (!plotCheck_17.checkForPlotInArea()) {
				break;
			}
			// Execute command
			plugin.flatMePlayers.getPlayer(uuid_17).sendLocalizedString("%commandMayTakeAWhile%", null);
			BlockChanger blockChanger_17 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid_17), plotCheck_17.getWorld());
			blockChanger_17.runWEregen(plotCheck_17.getPosX(), plotCheck_17.getPosY());
			plugin.flatMePlayers.getPlayer(uuid_17).sendLocalizedString("%plotRegenerated%", null);
			plugin.flatMePlayers.getPlayer(uuid_17).sendLocalizedString("%commandHasBeenQueued%", null);
			break;
		case "weclean":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_18 = (Player) sender;
			UUID uuid_18 = player_18.getUniqueId();
			plugin.flatMePlayers.add(uuid_18);
			// Security Check
			if (!plugin.securityCheck(plugin.flatMePlayers.getPlayer(uuid_18), args)) {
				break;
			}
			// PLOT CHECK
			PlotCheck plotCheck_18 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_18));
			if (!plotCheck_18.checkForCorrectWorld()) {
				break;
			}
			// TESTS
			int cmdCount_18 = 1;
			if (args.length > 1) {
				cmdCount_18 = parseCount(args[1]);
			}
			if (cmdCount_18 < 1) {
				sendLocalizedString(sender, returnCorrectUsage("weclean"), null);
				break;
			}
			// Execute command
			plugin.flatMePlayers.getPlayer(uuid_18).sendLocalizedString("%commandMayTakeAWhile%", null);
			int toDo_18 = 0;
			BlockChanger blockChanger_18 = new BlockChanger(plugin, plugin.flatMePlayers.getPlayer(uuid_18), plotCheck_18.getWorld());
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
			plugin.flatMePlayers.getPlayer(uuid_18).sendLocalizedString("%plotsCleaned%", args_18);
			plugin.flatMePlayers.getPlayer(uuid_18).sendLocalizedString("%commandHasBeenQueued%", null);
			PlayerQueue playerQueue_18 = plugin.flatMePlayers.getPlayer(uuid_18).getQueue();
			playerQueue_18.run();
			break;
		case "teleport":
			// PLAYER ONLY
			if (!(sender instanceof Player)) {
				sendLocalizedString(sender, "%commandOnlyPlayer%", null);
				break;
			}
			Player player_19 = (Player) sender;
			UUID uuid_19 = player_19.getUniqueId();
			plugin.flatMePlayers.add(uuid_19);
			// TESTS
			World world_19 = Bukkit.getWorld(plugin.config_world);
			if (world_19 == null) {
				String[] args_19 = { plugin.config_world };
				sendLocalizedString(sender, "%worldNotFound%", args_19);
				break;
			}
			int cmdPosX_19 = 0;
			cmdPosX_19 = parsePosition(args[1]);
			int cmdPosY_19 = 0;
			cmdPosY_19 = parsePosition(args[2]);
			plugin.getLogger().info(cmdPosX_19 + "," + cmdPosY_19);
			// PLOT CHECK
			PlotCheck plotCheck_19 = new PlotCheck(plugin, plugin.flatMePlayers.getPlayer(uuid_19));
			if (!plotCheck_19.checkForPlotInArea(cmdPosX_19, cmdPosY_19)) {
				plugin.flatMePlayers.getPlayer(uuid_19).sendLocalizedString("%wouldBeOutOfArea%", null);
				break;
			}
			// Execute command
			int portX_19 = cmdPosX_19 * plugin.config_jumpInterval;
			int portY_19 = cmdPosY_19 * plugin.config_jumpInterval;
			Location portLocation_19 = new Location(world_19, portX_19, (plugin.config_lvlHeight + 1), portY_19);
			portLocation_19.setYaw(-45F);
			player_19.teleport(portLocation_19);
			plugin.flatMePlayers.getPlayer(uuid_19).sendLocalizedString("%teleportToPlot%", null);
			break;
		default:
			// Execute command
			String[] args_x = { firstArg };
			sendLocalizedString(sender, "%commandError%", args_x);
			sendLocalizedString(sender, returnCorrectUsage("help"), null);
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
		commandList.add("check", new Command("flatme.admin", "/flatme check <page>", 0));
		commandList.add("claim", new Command("flatme.player", "/flatme claim", 0));
		commandList.add("clean", new Command("flatme.admin", "/flatme clean <count>", 1));
		commandList.add("create", new Command("flatme.admin", "/flatme create", 0));
		commandList.add("delete", new Command("flatme.admin", "/flatme delete", 0));
		commandList.add("extend", new Command("flatme.player", "/flatme extend", 0));
		commandList.add("help", new Command("flatme.player", "/flatme help [page]", 0));
		commandList.add("home", new Command("flatme.player", "/flatme home", 0));
		commandList.add("info", new Command("flatme.player", "/flatme info", 0));
		commandList.add("lock", new Command("flatme.admin", "/flatme lock", 0));
		commandList.add("regen", new Command("flatme.admin", "/flatme regen", 0));
		commandList.add("reload", new Command("flatme.admin", "/flatme reload", 0));
		commandList.add("remove", new Command("flatme.player", "/flatme remove <playername>", 1));
		commandList.add("teleport", new Command("flatme.admin", "/flatme teleport <x> <y>", 2));
		commandList.add("update", new Command("flatme.admin", "/flatme update", 0));
		commandList.add("version", new Command("flatme.player", "/flatme version", 0));
		commandList.add("weclean", new Command("flatme.admin", "/flatme weclean <count>", 1));
		commandList.add("weregen", new Command("flatme.admin", "/flatme weregen", 0));
		commandList.add("yes", new Command("flatme.player", "/flatme yes", 0));
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
