package de.sigmaroot.plugins;

import java.util.List;

public class CommandHandler {

	private List<Command> commandList;

	public List<Command> getCommandList() {
		return commandList;
	}

	public void setCommandList(List<Command> commandList) {
		this.commandList = commandList;
	}

	public CommandHandler() {
		super();
		this.commandList.add(new Command("help", "flatme.player", "/flatme help %page", 0));
	}

}
