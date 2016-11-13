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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This model manage a sequence of COMMAND and ARGs
 * 
 * @author Hao Jiang
 *
 */
public class LaTeXModel {

	private List<Token> tokens = new ArrayList<Token>();

	private HashMap<String, Set<Integer>> occurance = new HashMap<String, Set<Integer>>();

	public void add(String value, int offset) {
		this.add(new Token(value, offset));
	}

	/**
	 * This method does not guarantee no overlap between tokens. User should
	 * remove duplicated tokens before insert new one.
	 * 
	 * @param token
	 */
	public void add(Token token) {
		tokens.add(token);
		if (!occurance.containsKey(token.value)) {
			occurance.put(token.value, new HashSet<Integer>());
		}
		occurance.get(token.value).add(token.offset);
	}

	public void remove(int offset, int length) {
		int startToken = -1;
		int endToken = -1;
		for (int i = 0; i < tokens.size(); i++) {
			Token t = tokens.get(i);
			if (t.contains(offset) && startToken == -1) {
				startToken = i;
			}
			if (t.contains(offset + length - 1)) {
				endToken = i;
				break;
			}
			if (t.offset > offset + length) {
				endToken = i - 1;
				break;
			}
		}
		if (endToken == -1) {
			endToken = tokens.size() - 1;
		}
		if (startToken != -1 && endToken != -1) {
			for (int i = 0; i < endToken - startToken + 1; i++) {
				Token removed = tokens.remove(startToken);
				occurance.get(removed.value).remove(removed.offset);
			}
		}
	}

	public List<Token> tokens() {
		return Collections.unmodifiableList(tokens);
	}

	public boolean has(String key) {
		return occurance.containsKey(key) && occurance.get(key).size() != 0;
	}

	public static class Token {
		String value;
		int offset;

		public Token(String v, int o) {
			this.value = v;
			this.offset = o;
		}

		public boolean contains(int offset) {
			return this.offset <= offset && (this.offset + value.length()) > offset;
		}
	}
}
