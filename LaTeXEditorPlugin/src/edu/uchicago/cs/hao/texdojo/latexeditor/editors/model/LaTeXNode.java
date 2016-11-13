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

/**
 * @author Hao Jiang
 *
 */
public abstract class LaTeXNode {

	private int offset;

	private int length;

	private String content;

	public LaTeXNode(String c, int offset, int length) {
		super();
		this.content = c;
		this.offset = offset;
		this.length = length;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getEnd() {
		return getOffset() + getLength();
	}

	public boolean overlap(int offset, int length) {
		int end1 = this.offset + this.length - 1;
		int end2 = offset + length - 1;
		return (this.offset == offset && length != 0 && this.length != 0) || (this.offset < offset && end1 >= offset)
				|| (this.offset > offset && end2 >= this.offset);
	}

	public List<LaTeXNode> decompose() {
		List<LaTeXNode> nodes = new ArrayList<LaTeXNode>();
		nodes.add(this);
		return nodes;
	}
}
