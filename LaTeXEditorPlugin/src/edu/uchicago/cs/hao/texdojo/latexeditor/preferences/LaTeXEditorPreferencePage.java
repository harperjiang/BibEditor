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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class LaTeXEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public LaTeXEditorPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Settings for LaTeX Editor");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		// addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH,
		// "&Directory preference:", getFieldEditorParent()));
		// addField(new BooleanFieldEditor(PreferenceConstants.P_PRESERVE_CASE,
		// "&Preserve letter case in article titles",
		// getFieldEditorParent()));
		//
		// addField(new RadioGroupFieldEditor(PreferenceConstants.P_CHOICE, "An
		// example of a multiple-choice preference",
		// 1, new String[][] { { "&Choice 1", "choice1" }, { "C&hoice 2",
		// "choice2" } }, getFieldEditorParent()));

		addField(new StringFieldEditor(PreferenceConstants.P_LATEX_EXE, "Path for pdflatex executable",
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_BIBTEX_EXE, "Path for bibtex executable",
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_COMPILE_DOC,
				"Compile all .tex files containing \\begin{document}", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_MAIN_TEX, "Main tex file (when above is unselected)",
				getFieldEditorParent()));

		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}