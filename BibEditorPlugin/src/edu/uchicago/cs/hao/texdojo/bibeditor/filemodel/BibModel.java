/*******************************************************************************
 * Copyright (c) Oct 2016 Hao Jiang.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hao Jiang - initial API and implementation and/or initial documentation
 *******************************************************************************/

package edu.uchicago.cs.hao.texdojo.bibeditor.filemodel;

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

	public void merge(BibModel newmodel) {
		for (BibEntry e : newmodel.entries) {
			boolean found = false;
			for (int i = 0; i < entries.size(); i++) {
				if (entries.get(i).id.equals(e.id)) {
					BibEntry old = entries.get(i);
					entries.set(i, e);
					support.fireIndexedPropertyChange("entries", i, old, e);
					found = true;
					break;
				}
			}
			if (!found) {
				addEntry(e);
			}
		}
	}

	public void update(List<BibEntry> newEntries) {
		this.entries.clear();
		this.entries.addAll(newEntries);
		support.fireIndexedPropertyChange("entries", -1, null, null);
	}
}
