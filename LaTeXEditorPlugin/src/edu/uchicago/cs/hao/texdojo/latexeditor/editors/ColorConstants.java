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

package edu.uchicago.cs.hao.texdojo.latexeditor.editors;

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
	public static final String DEFAULT = "default";

	static {
		ColorRegistry cr = JFaceResources.getColorRegistry();
		cr.put(COMMAND, new RGB(100, 200, 50));
		cr.put(COMMAND_ARG, new RGB(100, 0, 200));
		cr.put(DEFAULT, new RGB(0, 0, 0));
	}

	public static Color get(String type) {
		return JFaceResources.getColorRegistry().get(type);
	}
}
