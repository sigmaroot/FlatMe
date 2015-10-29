package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;

public class CommandMap {

	private List<String> commandTexts;
	private List<Command> commands;

	public CommandMap() {
		super();
		commandTexts = new ArrayList<String>();
		commands = new ArrayList<Command>();
	}

	public List<String> getCommandTexts() {
		return commandTexts;
	}

	public void setCommandTexts(List<String> commandTexts) {
		this.commandTexts = commandTexts;
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public int size() {
		return commandTexts.size();
	}

	public void add(String commandText, Command command) {
		if (getCommand(commandText) == null) {
			commandTexts.add(commandText);
			commands.add(command);
		}
	}

	public void remove(int index) {
		commandTexts.remove(index);
		commands.remove(index);
	}

	public void remove(String index) {
		for (int i = 0; i < commandTexts.size(); i++) {
			if (commandTexts.get(i).equals(index)) {
				commandTexts.remove(i);
				commands.remove(i);
			}
		}
	}

	public void clear() {
		commandTexts.clear();
		commands.clear();
	}

	public Command getCommand(int index) {
		return commands.get(index);
	}

	public Command getCommand(String index) {
		for (int i = 0; i < commandTexts.size(); i++) {
			if (commandTexts.get(i).equals(index)) {
				return commands.get(i);
			}
		}
		return null;
	}

	public String getCommandText(int index) {
		return commandTexts.get(index);
	}

}
