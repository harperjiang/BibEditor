package edu.uchicago.cs.hao.texdojo.latexeditor.editors.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.model.LaTeXDocModel;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.GroupNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXNode;

public class LaTeXOutlineTreeContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof LaTeXDocModel) {
			LaTeXDocModel model = (LaTeXDocModel) inputElement;
			List<LaTeXOutlineTreeNode> roots = new ArrayList<LaTeXOutlineTreeNode>();
			LaTeXOutlineTreeNode current = null;

			for (LaTeXNode node : model.nodes()) {
				LaTeXOutlineTreeNode treeNode = LaTeXOutlineTreeNode.from(node);
				if (treeNode != null) {
					while (current != null && current.getLevel() >= treeNode.getLevel()) {
						current = current.getParent();
					}
					if (current == null) {
						roots.add(treeNode);
					} else {
						current.add(treeNode);
					}
					current = treeNode;
				}
			}

			return roots.toArray();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof LaTeXOutlineTreeNode) {
			return ((LaTeXOutlineTreeNode) parentElement).getChildren().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof LaTeXOutlineTreeNode) {
			return ((LaTeXOutlineTreeNode) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof LaTeXOutlineTreeNode) {
			return !((LaTeXOutlineTreeNode) element).getChildren().isEmpty();
		}
		return false;
	}

}
