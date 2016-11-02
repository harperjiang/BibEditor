package edu.uchicago.cs.hao.bibeditor.filemodel;

public class BibProp {
	public static final String TITLE = "title";
	public static final String YEAR = "year";
	public static final String AUTHOR = "author";
	String key;
	String value;

	public BibProp(String key, String val) {
		super();
		this.key = key.toLowerCase();
		this.value = val;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
