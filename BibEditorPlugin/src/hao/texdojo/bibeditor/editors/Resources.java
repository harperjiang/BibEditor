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

package hao.texdojo.bibeditor.editors;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;

/**
 * @author Hao Jiang
 *
 */
public class Resources {

	static final String COLOR_HIGHLIGHT = "highlight";

	static String COLOR_BROWN = "brown";

	static String COLOR_DARKBLUE = "darkBlue";

	static String COLOR_MAGENTA = "magenta";
	
	static String COLOR_GRASS = "grass";

	static String COLOR_WARNBACK = "warnBackground";

	static Color color(String name) {
		return JFaceResources.getColorRegistry().get(name);
	}
}
