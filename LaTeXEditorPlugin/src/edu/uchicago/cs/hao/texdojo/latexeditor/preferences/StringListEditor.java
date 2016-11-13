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

package edu.uchicago.cs.hao.texdojo.latexeditor.preferences;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * @author Hao Jiang
 *
 */
public class StringListEditor extends ListEditor {

	public StringListEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	@Override
	protected String createList(String[] items) {
		StringBuilder builder = new StringBuilder();
		for (String i : items) {
			builder.append(i).append(",");
		}
		return builder.toString();
	}

	@Override
	protected String getNewInputObject() {
		return "*.tmp";
	}

	@Override
	protected String[] parseString(String stringList) {
		return stringList.split(",");
	}

}
