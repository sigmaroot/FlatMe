package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;

public class CommandMap {

	private List<String> commandsText;
	private List<Command> commands;

	public List<String> getIndexes() {
		return commandsText;
	}

	public void setIndexes(List<String> indexes) {
		this.commandsText = indexes;
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public CommandMap() {
		super();
		commandsText = new ArrayList<String>();
		commands = new ArrayList<Command>();
	}

	public int size() {
		return commandsText.size();
	}

	public void add(String commandText, Command command) {
		commandsText.add(commandText);
		commands.add(command);
	}

	public void remove(int index) {
		commandsText.remove(index);
		commands.remove(index);
	}

	public void remove(String index) {
		for (int i = 0; i < commandsText.size(); i++) {
			if (commandsText.get(i).equals(index)) {
				commandsText.remove(i);
				commands.remove(i);
			}
		}
	}

	public Command getCommand(int index) {
		return commands.get(index);
	}

	public Command getCommand(String index) {
		for (int i = 0; i < commandsText.size(); i++) {
			if (commandsText.get(i).equals(index)) {
				return commands.get(i);
			}
		}
		return null;
	}

	public String getCommandText(int index) {
		return commandsText.get(index);
	}

}
