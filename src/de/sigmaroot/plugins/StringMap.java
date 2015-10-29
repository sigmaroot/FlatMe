package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;

public class StringMap {

	private List<String> indexes;
	private List<String> strings;

	public StringMap() {
		super();
		indexes = new ArrayList<String>();
		strings = new ArrayList<String>();
	}

	public List<String> getIndexes() {
		return indexes;
	}

	public void setIndexes(List<String> indexes) {
		this.indexes = indexes;
	}

	public List<String> getStrings() {
		return strings;
	}

	public void setStrings(List<String> strings) {
		this.strings = strings;
	}

	public int size() {
		return indexes.size();
	}

	public void add(String index, String string) {
		if (getString(index) == null) {
			indexes.add(index);
			strings.add(string);
		}
	}

	public void remove(int index) {
		indexes.remove(index);
		strings.remove(index);
	}

	public void remove(String index) {
		for (int i = 0; i < indexes.size(); i++) {
			if (indexes.get(i).equals(index)) {
				indexes.remove(i);
				strings.remove(i);
			}
		}
	}

	public void clear() {
		indexes.clear();
		strings.clear();
	}

	public String getString(int index) {
		return strings.get(index);
	}

	public String getString(String index) {
		for (int i = 0; i < indexes.size(); i++) {
			if (indexes.get(i).equals(index)) {
				return strings.get(i);
			}
		}
		return null;
	}

	public String getIndex(int index) {
		return indexes.get(index);
	}

}
