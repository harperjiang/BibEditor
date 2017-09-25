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

package edu.uchicago.cs.hao.texdojo.latexeditor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Hao Jiang
 *
 */
public class GroupNode extends LaTeXNode {

	private BeginNode begin;
	private EndNode end;
	private List<LaTeXNode> children = new ArrayList<LaTeXNode>();

	public GroupNode(BeginNode begin, EndNode end, List<LaTeXNode> children) {
		super(begin.getContent(), begin.getOffset(), end.getOffset() + end.getLength() - begin.getOffset());
		this.begin = begin;
		this.end = end;
		this.children = children;
	}

	@Override
	public List<LaTeXNode> decompose() {
		List<LaTeXNode> nodes = new ArrayList<LaTeXNode>();
		nodes.addAll(begin.decompose());
		for (LaTeXNode child : children)
			nodes.addAll(child.decompose());
		nodes.addAll(end.decompose());
		return nodes;
	}

	public List<LaTeXNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public boolean has(String command) {
		if (begin.has(command) || end.has(command))
			return true;
		for (LaTeXNode child : children)
			if (child.has(command))
				return true;
		return false;
	}
}
