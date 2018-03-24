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

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;

/**
 * @author Cathy
 *
 */
public class SpellCheckerPreference extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public SpellCheckerPreference() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Spell Checker for LaTeX Editor");
	}

	@Override
	protected void createFieldEditors() {
		addField(new ComboFieldEditor(PreferenceConstants.P_SPELLCHECKER, "Choose Spell Checker",
				new String[][] { new String[] { "ASpell", "aspell" }, new String[] { "No spell checker", "" } },
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_SPELLCHECKER_EXE, "Spell Checker Path",
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_SPELLCHECKER_OPTION, "Spell Checker Options",
				getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {

	}

}
