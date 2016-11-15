/*******************************************************************************
 * Copyright (c) 2016 Hao Jiang.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hao Jiang - initial API and implementation and/or initial documentation
 *******************************************************************************/

package edu.uchicago.cs.hao.texdojo.latexeditor.preferences;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;

/**
 * @author Cathy
 *
 */
public class ColorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public ColorPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set Syntax Color for LaTeX Editor");
	}

	@Override
	protected void createFieldEditors() {
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_COMMAND, "Command", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_ARG, "Argument({..})", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_OPTION, "Option([..])", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_COMMENT, "Comment", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_MATHMODE, "Math Mode", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_TEXT, "Text", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {

	}

}
