package edu.uchicago.cs.hao.bibeditor.filemodel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BibEntry {
	String type = "unknown";

	String id;

	List<BibProp> properties;

	Map<String, BibProp> propIndex;

	public BibEntry() {
		super();
		properties = new ArrayList<BibProp>();
		propIndex = new HashMap<String, BibProp>();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type.toLowerCase();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id.toLowerCase();
	}

	public List<BibProp> getProperties() {
		return properties;
	}

	public void addProperty(BibProp prop) {
		if (!this.propIndex.containsKey(prop.getKey())) {
			this.propIndex.put(prop.getKey(), prop);
			this.properties.add(prop);
		}
	}

	public String getProperty(String key) {
		BibProp prop = propIndex.get(key);
		return null == prop ? "" : prop.getValue();
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(MessageFormat.format("@{0}'{'{1},\n", type, id));
		for (BibProp p : properties) {
			builder.append(MessageFormat.format("{0} = '{'{1}'}',\n", p.key, p.value));
		}
		builder.append('}');
		
		return builder.toString();
	}
}
