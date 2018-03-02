package edu.uchicago.cs.hao.texdojo.latexeditor.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class GroupNodeTest {

	@Test
	public void testHas() {
		GroupNode gn = new GroupNode(new BeginNode("document", 0, 0, 5), new EndNode("document", 0, 0, 7),
				new ArrayList<LaTeXNode>());
		assertTrue(gn.has("document"));
	}

}
