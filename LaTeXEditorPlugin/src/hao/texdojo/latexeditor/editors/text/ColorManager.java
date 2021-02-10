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

package hao.texdojo.latexeditor.editors.text;

import static hao.texdojo.latexeditor.preferences.PreferenceConstants.*;
import static hao.texdojo.latexeditor.preferences.PreferenceInitializer.*;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import hao.texdojo.latexeditor.Activator;

/**
 * 
 * @author Hao Jiang
 *
 */
public class ColorManager {

	public static final String RED = "RED";
	static final String RED_VAL = StringConverter.asString(new RGB(255, 0, 0));

	public static final String BLACK = "BLACK";
	static final String BLACK_VAL = StringConverter.asString(new RGB(0, 0, 0));

	public static final String ORANGE = "ORANGE";
	static final String ORANGE_VAL = StringConverter.asString(new RGB(255, 127, 80));

	static final String keys[] = { P_COLOR_COMMAND, P_COLOR_ARG, P_COLOR_OPTION, P_COLOR_TEXT, P_COLOR_COMMENT,
			P_COLOR_MATHMODE, RED, BLACK ,ORANGE};
	static final String values[] = { DEFAULT_COLOR_COMMAND, DEFAULT_COLOR_ARG, DEFAULT_COLOR_OPTION, DEFAULT_COLOR_TEXT,
			DEFAULT_COLOR_COMMENT, DEFAULT_COLOR_MATHMODE, RED_VAL, BLACK_VAL ,ORANGE_VAL};

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
