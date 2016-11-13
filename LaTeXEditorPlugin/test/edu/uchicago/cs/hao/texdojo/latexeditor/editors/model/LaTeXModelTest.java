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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Hao Jiang
 *
 */
public class LaTeXModelTest {

	/**
	 * Test method for
	 * {@link edu.uchicago.cs.hao.texdojo.latexeditor.editors.model.LaTeXModel#add(java.lang.String, int)}.
	 */
	@Test
	public void testAddToken() {
		LaTeXModel model = new LaTeXModel();

		model.add("a1", 0);
		model.add("a3", 4);

		model.add("a5", 10);
		model.add("a3", 20);

		assertEquals(4, model.tokens().size());

		assertTrue(model.has("a1"));
		assertTrue(model.has("a3"));
		assertTrue(model.has("a5"));
	}

	/**
	 * Test method for
	 * {@link edu.uchicago.cs.hao.texdojo.latexeditor.editors.model.LaTeXModel#remove(int, int)}.
	 */
	@Test
	public void testRemove() {
		LaTeXModel model = new LaTeXModel();

		model.add("a1", 0);
		model.add("a3", 4);

		model.add("a5", 10);
		model.add("a3", 20);

		model.remove(4, 10);

		assertEquals(2, model.tokens().size());
		assertTrue(model.has("a1"));
		assertTrue(model.has("a3"));
		assertFalse(model.has("a5"));
	}

}
