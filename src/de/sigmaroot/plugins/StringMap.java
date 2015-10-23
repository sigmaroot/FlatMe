package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;

public class StringMap {

	private List<String> variables;
	private List<String> localizedStrings;

	public StringMap() {
		super();
		variables = new ArrayList<String>();
		localizedStrings = new ArrayList<String>();
	}

	public List<String> getVariables() {
		return variables;
	}

	public void setVariables(List<String> variables) {
		this.variables = variables;
	}

	public List<String> getLocalizedStrings() {
		return localizedStrings;
	}

	public void setLocalizedStrings(List<String> localizedStrings) {
		this.localizedStrings = localizedStrings;
	}

	public int size() {
		return variables.size();
	}

	public void add(String variable, String localizedString) {
		variables.add(variable);
		localizedStrings.add(localizedString);
	}

	public void remove(int index) {
		variables.remove(index);
		localizedStrings.remove(index);
	}

	public void remove(String index) {
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).equals(index)) {
				variables.remove(i);
				localizedStrings.remove(i);
			}
		}
	}

	public String getLocalizedString(int index) {
		return localizedStrings.get(index);
	}

	public String getLocalizedString(String index) {
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).equals(index)) {
				return localizedStrings.get(i);
			}
		}
		return null;
	}

	public String getVariable(int index) {
		return variables.get(index);
	}

}
