package edu.uchicago.cs.hao.texdojo.latexeditor.editors;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class ColorManager {

	public void dispose() {
	}

	public Color getColor(RGB rgb) {
		ColorRegistry cr = JFaceResources.getColorRegistry();
		if (null == cr.get(rgb.toString()))
			cr.put(rgb.toString(), rgb);
		return cr.get(rgb.toString());
	}
}
