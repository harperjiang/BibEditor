package hao.texdojo.latexeditor.editors.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import hao.texdojo.latexeditor.editors.model.LaTeXDocModel;
import hao.texdojo.latexeditor.model.GroupNode;
import hao.texdojo.latexeditor.model.LaTeXNode;

public class LaTeXFigureTreeContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof LaTeXDocModel) {
			LaTeXDocModel model = (LaTeXDocModel) inputElement;
			List<LaTeXFigureTreeNode> roots = new ArrayList<>();

			// Search for document node
			GroupNode document = null;

			for (LaTeXNode node : model.nodes()) {
				if (node instanceof GroupNode && "document".equals(node.getContent())) {
					document = (GroupNode) node;
					break;
				}
			}

			if (document != null)
				for (LaTeXNode node : document.getChildren()) {
					LaTeXFigureTreeNode fNode = LaTeXFigureTreeNode.from(node);
					if (fNode != null) {
						roots.add(fNode);
					}
				}
			return roots.toArray();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}

}
