package hao.texdojo.latexeditor.editors.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import hao.texdojo.latexeditor.Activator;
import hao.texdojo.latexeditor.ImageResource;

public class LaTeXFigureLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof LaTeXFigureTreeNode) {
			LaTeXFigureTreeNode lftNode = (LaTeXFigureTreeNode) element;
			switch (lftNode.getType()) {
			case LaTeXFigureTreeNode.FIGURE:
			case LaTeXFigureTreeNode.FIGURE_STAR:
				return Activator.getDefault().getImageRegistry().get(ImageResource.ICON_FIGURE);
			case LaTeXFigureTreeNode.TABLE:
				return Activator.getDefault().getImageRegistry().get(ImageResource.ICON_TABLE);
			case LaTeXFigureTreeNode.ALGORITHM:
				return Activator.getDefault().getImageRegistry().get(ImageResource.ICON_ALGO);
			default:
				return null;
			}
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof LaTeXFigureTreeNode) {
			LaTeXFigureTreeNode lftNode = (LaTeXFigureTreeNode) element;
			return lftNode.getTitle();
		}
		return super.getText(element);
	}
}
