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
public class InvokeNode extends LaTeXNode {

	private List<LaTeXNode> args = new ArrayList<LaTeXNode>();

	private CommandNode command;

	public InvokeNode(CommandNode command) {
		super(command.getContent(), command.getOffset(), command.getLength(), command.getLine());
		this.command = command;
		this.command.setParent(this);
	}

	public void attach(LaTeXNode arg) {
		if (arg instanceof ArgNode || arg instanceof OptionNode) {
			args.add(arg);
			arg.setParent(this);
			setLength(arg.getEnd() - command.getOffset());
		}
	}

	@Override
	public List<LaTeXNode> decompose() {
		List<LaTeXNode> components = new ArrayList<LaTeXNode>();
		components.addAll(command.decompose());
		for (LaTeXNode arg : args)
			components.addAll(arg.decompose());

		return components;
	}

	public List<LaTeXNode> getArgs() {
		return Collections.unmodifiableList(args);
	}

	public CommandNode getCommand() {
		return command;
	}

}
