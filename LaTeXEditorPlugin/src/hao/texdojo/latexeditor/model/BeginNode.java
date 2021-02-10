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

package hao.texdojo.latexeditor.model;

/**
 * @author Hao Jiang
 *
 */
public class BeginNode extends LaTeXNode {

	public BeginNode(String env, int offset, int length, int line) {
		super(env, offset, length, line);
	}

	public void append(ArgNode arg) {
		this.setContent(arg.getContent());
		this.setLength(getLength() + arg.getLength());
		arg.setParent(this);
	}

	public void append(OptionNode arg) {
		this.setContent(arg.getContent());
		this.setLength(getLength() + arg.getLength());
		arg.setParent(this);
	}

	@Override
	public String toString() {
		return String.format("\\begin{%s}", getContent());
	}
}
