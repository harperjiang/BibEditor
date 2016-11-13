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

package edu.uchicago.cs.hao.texdojo.latexeditor.editors.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.PartitionScanner;

/**
 * This model manage document as collections of <code>LaTeXNode</code>
 * 
 * @author Hao Jiang
 *
 */
public class LaTeXModel {

	private List<LaTeXNode> nodes = new ArrayList<LaTeXNode>();

	/**
	 * @param offset
	 * @param length
	 */
	public void clear(int offset, int length) {
		List<LaTeXNode> newnodes = new ArrayList<LaTeXNode>();
		for (LaTeXNode node : nodes) {
			if (node.overlap(offset, length)) {
				newnodes.addAll(node.decompose());
			} else {
				newnodes.add(node);
			}
		}
		nodes.clear();
		for (LaTeXNode node : newnodes) {
			if (!node.overlap(offset, length)) {
				nodes.add(node);
			}
		}
	}

	/**
	 * @param tokens
	 */
	public void update(IDocument doc, ITypedRegion[] tokens) throws BadLocationException {
		if (tokens.length == 0)
			return;
		// Parse Tokens
		List<LaTeXNode> newnodes = parseTokens(doc, tokens);

		int offset = tokens[0].getOffset();
		int insertIndex = 0;

		if (nodes.get(0).getOffset() > offset)
			insertIndex = 0;
		else if (nodes.get(nodes.size() - 1).getEnd() <= offset)
			insertIndex = nodes.size();
		else {
			for (int i = 0; i < nodes.size() - 1; i++) {
				if (nodes.get(i).getOffset() < offset && nodes.get(i + 1).getOffset() > offset) {
					insertIndex = i + 1;
					break;
				}
			}
		}
		nodes.addAll(insertIndex, newnodes);

		parseNodes();
	}

	public boolean has(String command) {

	}

	protected void parseNodes() {
		List<LaTeXNode> newnodes = new ArrayList<LaTeXNode>();
		Stack<LaTeXNode> stacks = new 
	}

	protected List<LaTeXNode> parseTokens(IDocument doc, ITypedRegion[] tokens) throws BadLocationException {
		List<LaTeXNode> newnodes = new ArrayList<LaTeXNode>();
		for (ITypedRegion token : tokens) {
			String data = doc.get(token.getOffset(), token.getLength());
			if (PartitionScanner.LATEX_COMMAND.equals(token.getType())) {
				if (LaTeXConstant.BEGIN.equals(data)) {
					newnodes.add(new BeginNode(null, token.getOffset(), token.getLength()));
				} else if (LaTeXConstant.END.equals(data)) {
					newnodes.add(new EndNode(null, token.getOffset(), token.getLength()));
				} else {
					newnodes.add(new CommandNode(data, token.getOffset(), token.getLength()));
				}
			} else if (PartitionScanner.LATEX_ARG.equals(token.getType())) {
				newnodes.add(new ArgNode(data, token.getOffset(), token.getLength()));
			} else if (PartitionScanner.LATEX_OPTION.equals(token.getType())) {
				newnodes.add(new OptionNode(data, token.getOffset(), token.getLength()));
			} else if (IDocument.DEFAULT_CONTENT_TYPE.equals(token.getType())) {
				newnodes.add(new TextNode(data, token.getOffset(), token.getLength()));
			} else {
				throw new IllegalArgumentException("Unrecognized item type:" + token.getType());
			}
		}
		return newnodes;
	}

}
