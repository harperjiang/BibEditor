package edu.uchicago.cs.hao.texdojo.latexeditor.editors.outline;

import java.util.ArrayList;
import java.util.List;

import edu.uchicago.cs.hao.texdojo.latexeditor.model.CommandNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.InvokeNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXNode;

public class LaTeXTreeNode {

	public static final String CHAPTER = "chapter";

	public static final String SECTION = "section";

	public static final String SUBSECTION = "subsection";

	public static final String SUBSUBSECTION = "subsubsection";

	private String text;
	private int level;
	private LaTeXNode node;

	private LaTeXTreeNode parent;
	private List<LaTeXTreeNode> children = new ArrayList<LaTeXTreeNode>();

	private LaTeXTreeNode(String text, int level, LaTeXNode node) {
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

	public void add(LaTeXTreeNode child) {
		child.parent = this;
		children.add(child);
	}

	public LaTeXTreeNode getParent() {
		return parent;
	}

	public List<LaTeXTreeNode> getChildren() {
		return children;
	}

	public static LaTeXTreeNode from(LaTeXNode node) {
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
			case CHAPTER:
				return new LaTeXTreeNode(text, 0, node);
			case SECTION:
				return new LaTeXTreeNode(text, 1, node);
			case SUBSECTION:
				return new LaTeXTreeNode(text, 2, node);
			case SUBSUBSECTION:
				return new LaTeXTreeNode(text, 3, node);
			default:
				return null;
			}
		}
		return null;
	}

}
