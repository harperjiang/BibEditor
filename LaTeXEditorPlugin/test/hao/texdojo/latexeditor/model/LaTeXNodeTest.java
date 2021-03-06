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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import hao.texdojo.latexeditor.model.ArgNode;
import hao.texdojo.latexeditor.model.BeginNode;
import hao.texdojo.latexeditor.model.CommandNode;
import hao.texdojo.latexeditor.model.EndNode;
import hao.texdojo.latexeditor.model.GroupNode;
import hao.texdojo.latexeditor.model.InvokeNode;
import hao.texdojo.latexeditor.model.LaTeXNode;
import hao.texdojo.latexeditor.model.TextNode;

/**
 * @author Hao Jiang
 *
 */
public class LaTeXNodeTest {

	/**
	 * Test method for
	 * {@link hao.texdojo.latexeditor.model.LaTeXNode#overlap(int, int)}.
	 */
	@Test
	public void testOverlap() {
		LaTeXNode nodeb = new TextNode("def", 10, 20, 10);

		assertFalse(nodeb.overlap(0, 10));
		assertTrue(nodeb.overlap(0, 11));
		assertTrue(nodeb.overlap(11, 5));
		assertTrue(nodeb.overlap(19, 10));
		assertTrue(nodeb.overlap(0, 100));
		assertFalse(nodeb.overlap(30, 19));
	}

	@Test
	public void testHas() {
		TextNode tn = new TextNode("document", 10, 20, 10);

		assertTrue(tn.has("document"));
	}

	@Test
	public void testDecompose() {
		TextNode tn = new TextNode("document", 10, 20, 10);
		List<LaTeXNode> dtn = tn.decompose();
		assertEquals(1, dtn.size());
		assertTrue(dtn.get(0) == tn);

		List<LaTeXNode> nodes = new ArrayList<LaTeXNode>();
		nodes.add(new TextNode("aadsds", 10, 20, 190));

		InvokeNode inv = new InvokeNode(new CommandNode("aka", 10, 20, 10));
		inv.attach(new ArgNode("ttd", 20, 30, 10));
		inv.attach(new ArgNode("mma", 30, 40, 10));
		nodes.add(inv);

		dtn.add(new GroupNode(new BeginNode("ddt", 20, 1, 10), new EndNode("ddt", 30, 1, 20), nodes));

		GroupNode a = new GroupNode(new BeginNode("abc", 0, 10, 4), new EndNode("def", 50, 100, 10), dtn);
		List<LaTeXNode> da = a.decompose();

		assertEquals(9, da.size());
	}
}
