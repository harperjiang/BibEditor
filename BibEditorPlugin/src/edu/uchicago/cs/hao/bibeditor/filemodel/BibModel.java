package edu.uchicago.cs.hao.bibeditor.filemodel;

import java.util.ArrayList;
import java.util.List;

public class BibModel {

	private List<BibEntry> entries;

	public BibModel() {
		super();
		entries = new ArrayList<BibEntry>();
	}

	public List<BibEntry> getEntries() {
		return entries;
	}

}
