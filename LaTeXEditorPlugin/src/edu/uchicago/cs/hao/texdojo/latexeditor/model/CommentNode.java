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

/**
 * @author Hao Jiang
 *
 */
public class CommentNode extends LaTeXNode {
	/**
	 * 
	 */
	public CommentNode(String data, int offset, int length, int line) {
		super(data, offset, length, line);
	}
}
