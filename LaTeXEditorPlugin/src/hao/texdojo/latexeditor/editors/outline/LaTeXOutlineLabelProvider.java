package hao.texdojo.latexeditor.editors.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import hao.texdojo.latexeditor.Activator;
import hao.texdojo.latexeditor.ImageResource;

public class LaTeXOutlineLabelProvider extends LabelProvider {

	public Image getImage(Object element) {
		if (element instanceof LaTeXOutlineTreeNode) {
			switch (((LaTeXOutlineTreeNode) element).getLevel()) {
			case 0:// part
			case 1:// chapter
				return Activator.getDefault().getImageRegistry().get(ImageResource.ICON_CHAPTER);
			case 2:// section
				return Activator.getDefault().getImageRegistry().get(ImageResource.ICON_LIST);
			case 3:// subsection
			case 4:// subsubsection
				return Activator.getDefault().getImageRegistry().get(ImageResource.ICON_ITEM);
			default:
				return Activator.getDefault().getImageRegistry().get(ImageResource.ICON_ITEM);
			}
		}
		return null;
	}

	public String getText(Object element) {
		if (element instanceof LaTeXOutlineTreeNode) {
			return ((LaTeXOutlineTreeNode) element).getText();
		}
		return null;
	}
}
