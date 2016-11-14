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

import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COLOR_ARG;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COLOR_COMMAND;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COLOR_COMMENT;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COLOR_OPTION;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COLOR_TEXT;

import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.LaTeXEditor;

/**
 * @author Hao Jiang
 *
 */
public class PreferenceChangeMonitor implements IPreferenceChangeListener {

	@Override
	public void preferenceChange(PreferenceChangeEvent event) {
		if (P_COLOR_COMMAND.equals(event.getKey()) || P_COLOR_ARG.equals(event.getKey())
				|| P_COLOR_OPTION.equals(event.getKey()) || P_COLOR_COMMENT.equals(event.getKey())
				|| P_COLOR_TEXT.equals(event.getKey())) {
			// Put new color to registry
			ColorRegistry cr = JFaceResources.getColorRegistry();
			cr.put(event.getKey(), StringConverter.asRGB(event.getNewValue().toString()));

			// Refresh Current Active Editor
			IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (editor instanceof LaTeXEditor) {
				LaTeXEditor latexEditor = (LaTeXEditor) editor;
				latexEditor.refresh();
			}

		}
	}

}
