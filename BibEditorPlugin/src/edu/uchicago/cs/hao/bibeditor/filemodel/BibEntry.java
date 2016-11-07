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

package edu.uchicago.cs.hao.bibeditor.filemodel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class BibEntry implements IAdaptable {

	// Data Model
	String type = "unknown";

	String id;

	List<BibProp> properties;

	Map<String, BibProp> propIndex;

	// UI Property
	boolean highlight = false;

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

	public void setPreserveCase(boolean preserveCase) {
		if (preserveCase) {
			String title = this.getProperty(EntryType.title);
			if (!title.isEmpty() && !(title.charAt(0) == '{' && title.charAt(title.length() - 1) == '}')) {
				propIndex.get(EntryType.title).setValue("{" + title + "}");
			}
		}
	}

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	private EntryPropertySource psource = new EntryPropertySource();

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == IPropertySource.class) {
			return (T) psource;
		}
		return null;
	}

	public class EntryPropertySource implements IPropertySource {

		static final int PID_TYPE = 0;
		static final int PID_ID = 1;
		static final int PID_TITLE = 2;
		static final int PID_AUTHOR = 3;
		static final int PID_YEAR = 4;

		@Override
		public IPropertyDescriptor[] getPropertyDescriptors() {
			return new PropertyDescriptor[] { new PropertyDescriptor(PID_TYPE, "Type"),
					new PropertyDescriptor(PID_ID, "cite_id"), new PropertyDescriptor(PID_TITLE, "Title"),
					new PropertyDescriptor(PID_AUTHOR, "Author"), new PropertyDescriptor(PID_YEAR, "Year"), };
		}

		@Override
		public Object getPropertyValue(Object id) {
			switch ((int) id) {
			case PID_TYPE:
				return getType();
			case PID_ID:
				return getId();
			case PID_TITLE:
				return getProperty(EntryType.title);
			case PID_AUTHOR:
				return getProperty(EntryType.author);
			case PID_YEAR:
				return getProperty(EntryType.year);
			default:
				return null;
			}
		}

		@Override
		public Object getEditableValue() {
			return null;
		}

		@Override
		public void resetPropertyValue(Object id) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isPropertySet(Object id) {
			return false;
		}

		@Override
		public void setPropertyValue(Object id, Object value) {
			throw new UnsupportedOperationException();
		}

	}
}
