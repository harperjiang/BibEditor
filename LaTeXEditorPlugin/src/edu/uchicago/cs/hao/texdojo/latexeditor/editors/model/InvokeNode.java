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
public class InvokeNode extends LaTeXNode {

	private List<LaTeXNode> args = new ArrayList<LaTeXNode>();

	private CommandNode command;

	public InvokeNode(CommandNode command, List<LaTeXNode> args) {
		super(command.getContent(), command.getOffset(), args.get(args.size() - 1).getEnd() - command.getOffset());
	}

	@Override
	public List<LaTeXNode> decompose() {
		List<LaTeXNode> components = new ArrayList<LaTeXNode>();
		components.addAll(command.decompose());
		for (LaTeXNode arg : args)
			components.addAll(arg.decompose());

		return components;
	}
}
