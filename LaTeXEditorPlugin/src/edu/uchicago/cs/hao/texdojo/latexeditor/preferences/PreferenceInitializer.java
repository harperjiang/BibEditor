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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.*;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public static final String DEFAULT_LATEX_EXE = "pdflatex";

	public static final String DEFAULT_BIB_EXE = "bibtex";

	public static final String DEFAULT_TEMP_FILE = "*.aux,*.log";

	public static final boolean DEFAULT_COMPILE_DOCUMENT = true;

	public static final String DEFAULT_MAIN_TEX = "main.tex";

	public static final String DEFAULT_COLOR_COMMAND = StringConverter.asString(new RGB(18, 45, 89));

	public static final String DEFAULT_COLOR_ARG = StringConverter.asString(new RGB(101, 35, 42));

	public static final String DEFAULT_COLOR_OPTION = StringConverter.asString(new RGB(109, 70, 22));

	public static final String DEFAULT_COLOR_COMMENT = StringConverter.asString(new RGB(48, 112, 29));

	public static final String DEFAULT_COLOR_TEXT = StringConverter.asString(new RGB(0, 0, 0));

	public static final String DEFAULT_COLOR_MATHMODE = StringConverter.asString(new RGB(250, 10, 50));

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(P_LATEX_EXE, DEFAULT_LATEX_EXE);
		store.setDefault(P_BIBTEX_EXE, DEFAULT_BIB_EXE);
		store.setDefault(P_TEMP_FILE, DEFAULT_TEMP_FILE);
		store.setDefault(P_COMPILE_DOC, DEFAULT_COMPILE_DOCUMENT);
		store.setDefault(P_MAIN_TEX, DEFAULT_MAIN_TEX);
		store.setDefault(P_COLOR_COMMAND, DEFAULT_COLOR_COMMAND);
		store.setDefault(P_COLOR_COMMENT, DEFAULT_COLOR_COMMENT);
		store.setDefault(P_COLOR_OPTION, DEFAULT_COLOR_OPTION);
		store.setDefault(P_COLOR_ARG, DEFAULT_COLOR_ARG);
		store.setDefault(P_COLOR_TEXT, DEFAULT_COLOR_TEXT);
		store.setDefault(P_COLOR_MATHMODE, DEFAULT_COLOR_MATHMODE);
		store.setDefault(P_LINE_WRAP, -1);
	}

}
