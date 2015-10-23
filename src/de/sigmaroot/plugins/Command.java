package de.sigmaroot.plugins;

public class Command {

	private String permission;
	private String usage;
	private int expectedArgs;

	public Command(String permission, String usage, int expectedArgs) {
		super();
		this.permission = permission;
		this.usage = usage;
		this.expectedArgs = expectedArgs;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public int getExpectedArgs() {
		return expectedArgs;
	}

	public void setExpectedArgs(int expectedArgs) {
		this.expectedArgs = expectedArgs;
	}

	public boolean enoughArguments(String[] args) {
		if ((args.length - 1) < expectedArgs) {
			return false;
		}
		return true;
	}

}
