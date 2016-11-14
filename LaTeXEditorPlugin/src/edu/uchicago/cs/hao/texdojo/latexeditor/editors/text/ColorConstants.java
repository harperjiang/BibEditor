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

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * 
 * @author Hao Jiang
 *
 */
public class ColorConstants {
	
	public static final String COMMAND = "command";
	public static final String COMMAND_ARG = "command_arg";
	public static final String OPTION = "option";
	public static final String COMMENT = "comment";
	public static final String DEFAULT = "default";

	static {
		ColorRegistry cr = JFaceResources.getColorRegistry();
		cr.put(COMMAND, new RGB(100, 200, 50));
		cr.put(COMMAND_ARG, new RGB(100, 0, 200));
		cr.put(OPTION, new RGB(150, 50, 100));
		cr.put(DEFAULT, new RGB(0, 0, 0));
		cr.put(COMMENT, new RGB(240, 10, 20));
	}

	public static Color get(String type) {
		return JFaceResources.getColorRegistry().get(type);
	}
}
