package edu.uchicago.cs.hao.bibeditor.filemodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BibModel {

	private List<BibEntry> entries;

	public BibModel() {
		super();
		support = new PropertyChangeSupport(this);
		entries = new ArrayList<BibEntry>();
	}

	public List<BibEntry> getEntries() {
		return Collections.unmodifiableList(entries);
	}

	public void addEntry(BibEntry entry) {
		this.entries.add(entry);
		support.firePropertyChange("addEntry", null, entry);
	}

	public void removeEntry(BibEntry entry) {
		this.entries.remove(entry);
		support.firePropertyChange("removeEntry", entry, null);
	}

	public void replaceEntry(BibEntry old, BibEntry newe) {
		int index = this.entries.indexOf(old);
		if (-1 != index) {
			this.entries.set(index, newe);
			support.fireIndexedPropertyChange("entries", index, old, newe);
		}
	}

	private PropertyChangeSupport support;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

}
