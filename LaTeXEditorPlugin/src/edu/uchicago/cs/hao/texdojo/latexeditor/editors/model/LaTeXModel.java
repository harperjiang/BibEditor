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
public class LaTeXModel {

	private List<Token> tokens = new ArrayList<Token>();
	
	public void add(String value, int offset) {
		this.add(new Token(value,offset));
	}
	
	public void add(Token token) {
		// Check whether this token already exists
	}
	
	public void remove(int offset, int length) {
		
	}
	
	public static class Token {
		String value;
		int offset;
		
		public Token(String v, int o) {
			this.value = v;
			this.offset = o;
		}
	}
}
