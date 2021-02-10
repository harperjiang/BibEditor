package hao.texdojo.latexeditor.editors.outline;

import hao.texdojo.latexeditor.model.GroupNode;
import hao.texdojo.latexeditor.model.InvokeNode;
import hao.texdojo.latexeditor.model.LaTeXNode;

public class LaTeXFigureTreeNode implements ILaTeXTreeNode {

	static final String FIGURE = "figure";
	static final String FIGURE_STAR = "figure*";
	static final String TABLE = "table";
	static final String ALGORITHM = "algorithm";

	private String type;
	private String title;
	private LaTeXNode node;

	public LaTeXFigureTreeNode(String type, String title, LaTeXNode node) {
		this.type = type;
		this.title = title;
		this.node = node;
	}

	@Override
	public LaTeXNode getNode() {
		return node;
	}

	public String getTitle() {
		return title;
	}

	public String getType() {
		return type;
	}

	public static LaTeXFigureTreeNode from(LaTeXNode node) {
		if (node instanceof GroupNode) {
			GroupNode gn = (GroupNode) node;
			switch (gn.getContent()) {
			case FIGURE:
			case FIGURE_STAR:
			case TABLE:
			case ALGORITHM:
				for (LaTeXNode n : gn.getChildren()) {
					if (n instanceof InvokeNode && "caption".equals(n.getContent())) {
						InvokeNode captionNode = (InvokeNode) n;
						return new LaTeXFigureTreeNode(gn.getContent(), captionNode.getArgs().get(0).getContent(), gn);
					}
				}
			default:
				return null;
			}
		}
		return null;
	}
}
