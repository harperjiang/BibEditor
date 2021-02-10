package hao.texdojo.latexeditor.editors.outline;

import java.util.ArrayList;
import java.util.List;

import hao.texdojo.latexeditor.model.CommandNode;
import hao.texdojo.latexeditor.model.InvokeNode;
import hao.texdojo.latexeditor.model.LaTeXNode;

public class LaTeXOutlineTreeNode implements ILaTeXTreeNode {

	public static final String PART = "part";

	public static final String CHAPTER = "chapter";

	public static final String SECTION = "section";

	public static final String SUBSECTION = "subsection";

	public static final String SUBSUBSECTION = "subsubsection";

	private String text;
	private int level;
	private LaTeXNode node;

	private LaTeXOutlineTreeNode parent;
	private List<LaTeXOutlineTreeNode> children = new ArrayList<LaTeXOutlineTreeNode>();

	private LaTeXOutlineTreeNode(String text, int level, LaTeXNode node) {
		this.text = text;
		this.level = level;
		this.node = node;
	}

	public LaTeXNode getNode() {
		return node;
	}

	public String getText() {
		return text;
	}

	public int getLevel() {
		return level;
	}

	public void add(LaTeXOutlineTreeNode child) {
		child.parent = this;
		children.add(child);
	}

	public LaTeXOutlineTreeNode getParent() {
		return parent;
	}

	public List<LaTeXOutlineTreeNode> getChildren() {
		return children;
	}

	public static LaTeXOutlineTreeNode from(LaTeXNode node) {
		if (node instanceof InvokeNode || node instanceof CommandNode) {
			String text = "";
			if (node instanceof InvokeNode) {
				try {
					InvokeNode inv = (InvokeNode) node;
					text = inv.getArgs().get(0).getContent();
				} catch (Exception e) {
					text = "";
				}
			}
			switch (node.getContent()) {
			case PART:
				return new LaTeXOutlineTreeNode(text, 0, node);
			case CHAPTER:
				return new LaTeXOutlineTreeNode(text, 1, node);
			case SECTION:
				return new LaTeXOutlineTreeNode(text, 2, node);
			case SUBSECTION:
				return new LaTeXOutlineTreeNode(text, 3, node);
			case SUBSUBSECTION:
				return new LaTeXOutlineTreeNode(text, 4, node);
			default:
				return null;
			}
		}
		return null;
	}

}
