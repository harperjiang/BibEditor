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

package edu.uchicago.cs.hao.texdojo.latexeditor.editors.text;

import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.*;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.*;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Color;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;

/**
 * 
 * @author Hao Jiang
 *
 */
public class ColorManager {

	static final String keys[] = { P_COLOR_COMMAND, P_COLOR_ARG, P_COLOR_OPTION, P_COLOR_TEXT, P_COLOR_COMMENT,
			P_COLOR_MATHMODE };
	static final String values[] = { DEFAULT_COLOR_COMMAND, DEFAULT_COLOR_ARG, DEFAULT_COLOR_OPTION, DEFAULT_COLOR_TEXT,
			DEFAULT_COLOR_COMMENT, DEFAULT_COLOR_MATHMODE };

	static {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		ColorRegistry cr = JFaceResources.getColorRegistry();
		for (int i = 0; i < keys.length; i++) {
			cr.put(keys[i], StringConverter.asRGB(prefs.get(keys[i], values[i])));
		}
	}

	public static Color get(String type) {
		return JFaceResources.getColorRegistry().get(type);
	}
}
