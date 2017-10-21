package edu.uchicago.cs.hao.texdojo.latexeditor.editors.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class LaTeXOutlineLabelProvider extends LabelProvider {
	protected Image chapterImage = new Image(Display.getCurrent(),
			Thread.currentThread().getContextClassLoader().getResourceAsStream("icons/chapter_icon.png"));

	protected Image listImage = new Image(Display.getCurrent(),
			Thread.currentThread().getContextClassLoader().getResourceAsStream("icons/list_icon.png"));

	public Image getImage(Object element) {
		if (element instanceof LaTeXTreeNode) {
			switch (((LaTeXTreeNode) element).getLevel()) {
			case 0:
				return chapterImage;
			default:
				return listImage;
			}
		}
		return null;
	}

	public String getText(Object element) {
		if (element instanceof LaTeXTreeNode) {
			return ((LaTeXTreeNode) element).getText();
		}
		return null;
	}

	@Override
	public void dispose() {
		super.dispose();
		listImage.dispose();
		chapterImage.dispose();
	}
}
